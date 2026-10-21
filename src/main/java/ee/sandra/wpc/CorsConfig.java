package ee.sandra.wpc;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Millistelt aadressidelt tohib frontend backendiga suhelda.
// Kehtib kõigile kontrolleritele korraga, nii et igaühele eraldi @CrossOrigin-it pole vaja.
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200", "http://lvh.me:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}