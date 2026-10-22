package ee.sandra.wpc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching // lülitab sisse @Cacheable ja @CacheEvict
public class WpcApplication {

    public static void main(String[] args) {
        SpringApplication.run(WpcApplication.class, args);
    }

}
