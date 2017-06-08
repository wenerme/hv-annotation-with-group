package me.wener.issues.demo;

import java.util.Optional;
import javax.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import me.wener.issues.demo.validator.ValidatorHelper;
import org.hibernate.validator.internal.engine.ValidatorImpl;
import org.joor.Reflect;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StopWatch;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@Slf4j
@EnableSwagger2
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public Docket swaggerSpringfoxDocket() {
        StopWatch watch = new StopWatch();
        watch.start();
        Contact contact = new Contact(
            "wener",
            "https://github.com/wenerme/hv-annotation-with-group",
            "");

        ApiInfo apiInfo = new ApiInfo(
            "hv-annotation-with-group",
            "",
            "1",
            "",
            contact,
            "MIT",
            "");

        Docket docket = new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo)
            .forCodeGeneration(true)
            .select()
            .paths(v -> !v.startsWith("/error"))
            .build();
        watch.stop();
        log.debug("Started Swagger in {} ms", watch.getTotalTimeMillis());
        return docket;
    }

    @Bean
    public CommandLineRunner patchValidatorCommandLineRunner(
        Optional<Validator> validatorOpt
    ) {
        return args -> {
            Validator validator = validatorOpt.orElse(null);
            Validator target = validator;

            if (validator == null) {
                log.info("Validator not found, ignore patch");
                return;
            }

            if (validator instanceof SpringValidatorAdapter) {
                log.info("Validator patch SpringValidatorAdapter");
                target = Reflect.on(validator).get("targetValidator");
            }

            if (target instanceof ValidatorImpl) {
                log.info("Validator patch ValidatorImpl");
                ValidatorHelper.patchHibernateValidator(target);
            } else {
                log.warn("Validator unsupported for patch");
            }
        };
    }
}
