package org.container.terraman.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.vault.core.VaultTemplate;

//@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
@SpringBootApplication
//@ComponentScan(basePackageClasses = VaultTemplate.class)
public class TerramanApplication {
    public static void main(String[] args) {
        SpringApplication.run(TerramanApplication.class, args);
    }

}
