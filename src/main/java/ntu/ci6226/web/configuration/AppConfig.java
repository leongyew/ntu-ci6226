package ntu.ci6226.web.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

/**
 * Created by alexto on 8/3/16.
 */

@Configuration
@EnableWebMvc
@Import({MvcConfig.class})
@ComponentScan(basePackages = "ntu.ci6226")
public class AppConfig {
    @Bean
    public ServletContextTemplateResolver getTemplateResolver() {
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver();
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine getTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(getTemplateResolver());
        return templateEngine;
    }

    @Bean
    public ThymeleafViewResolver getThymeleafViewResolver() {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(getTemplateEngine());
        return viewResolver;
    }

    @Bean
    public MultipartResolver getMultipartResolver() {
        return new CommonsMultipartResolver();
    }
}
