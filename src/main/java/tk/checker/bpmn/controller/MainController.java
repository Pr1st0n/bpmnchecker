package tk.checker.bpmn.controller;

import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tk.checker.bpmn.model.Process;
import tk.checker.bpmn.service.BpmnParser;
import tk.checker.bpmn.service.BpmnVerifier;
import tk.checker.bpmn.utils.exceptions.BpmnParseException;
import tk.checker.bpmn.utils.exceptions.BpmnVerifyException;


import static org.springframework.http.HttpStatus.*;

@RestController
public class MainController {
    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    public ResponseEntity<JSONObject> uploadFile(@RequestParam("file") MultipartFile file) {
        JSONObject responseBody = new JSONObject();

        if (!file.isEmpty()) {
            try {
                Process process = BpmnParser.parse(file);

                boolean result = BpmnVerifier.verify(process);

                System.out.println(result);

                responseBody.put("result", "Model is valid");
                responseBody.put("valid", true);
            }
            catch (BpmnParseException e) {
                responseBody.put("result", "Verification failed: " + e.getCause().getMessage());

                return new ResponseEntity<>(responseBody, OK);
            }
            catch (BpmnVerifyException e) {
                responseBody.put("result", "Verification failed: " + e.getCause().getMessage());

                return new ResponseEntity<>(responseBody, OK);
            }
        }
        else {
            responseBody.put("result", "Invalid request: file is empty");

            return new ResponseEntity<>(responseBody, I_AM_A_TEAPOT);
        }

        return new ResponseEntity<>(responseBody, OK);
    }
}
