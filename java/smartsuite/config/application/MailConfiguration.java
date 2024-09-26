package smartsuite.config.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import smartsuite.app.common.mail.sender.JavaSmtpMailSender;

@Configuration
public class MailConfiguration {

    @Bean("mailSender")
    public JavaSmtpMailSender javaSmtpMailSender(){
        return new JavaSmtpMailSender();
    }
}
