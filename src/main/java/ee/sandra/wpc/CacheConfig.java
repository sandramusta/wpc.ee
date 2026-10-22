package ee.sandra.wpc;

import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.LoggingCacheErrorHandler;
import org.springframework.context.annotation.Configuration;

// Kui Redis ei tööta, ei lähe leht katki: viga kirjutatakse logisse
// ja andmed võetakse otse andmebaasist nagu ilma vahemäluta.
@Configuration
public class CacheConfig implements CachingConfigurer {

    @Override
    public CacheErrorHandler errorHandler() {
        return new LoggingCacheErrorHandler();
    }
}
