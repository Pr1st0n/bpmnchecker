package tk.checker.bpmn.controller;

import com.google.gson.Gson;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tk.checker.bpmn.model.CommonVerificationError;
import tk.checker.bpmn.model.CommonVerificationError.VerificationErrorType;
import tk.checker.bpmn.model.Process;
import tk.checker.bpmn.service.BpmnParser;
import tk.checker.bpmn.service.BpmnVerifier;
import tk.checker.bpmn.utils.BpmnParseException;


import static org.springframework.http.HttpStatus.*;

@RestController
public class MainController {
    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    public ResponseEntity<String> testModel(@RequestParam("file") MultipartFile file) {
        String responseBody;
        Gson gson = new Gson();

        if (!file.isEmpty()) {
            try {
                Process process = BpmnParser.parse(file);

                Process result = BpmnVerifier.verify(process);

                System.out.println(result);

                responseBody = gson.toJson(result.getErrorList());
            }
            catch (BpmnParseException e) {
                responseBody = gson.toJson(new CommonVerificationError(VerificationErrorType.OTHER, e.getMessage()));

                return new ResponseEntity<>(responseBody, OK);
            }
        }
        else {
            responseBody = gson.toJson(new CommonVerificationError(VerificationErrorType.OTHER, "Invalid request: file is empty"));

            return new ResponseEntity<>(responseBody, I_AM_A_TEAPOT);
        }

        return new ResponseEntity<>(responseBody, OK);
    }
}
