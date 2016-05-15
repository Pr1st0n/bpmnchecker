package tk.checker.bpmn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return container -> container.setPort(8095);
    }

    public static void main(String[] args) throws Throwable {
        SpringApplication.run(new Object[]{Application.class}, args);
    }
}
