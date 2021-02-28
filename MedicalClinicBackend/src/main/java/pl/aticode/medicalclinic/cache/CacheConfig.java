package pl.aticode.medicalclinic.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.sf.ehcache.config.CacheConfiguration;

@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {
    
    @Bean
    public net.sf.ehcache.CacheManager ehCacheManager() {
        CacheConfiguration companyDataCache = new CacheConfiguration();
        companyDataCache.setName("companyDataCache");
        companyDataCache.setMemoryStoreEvictionPolicy("LRU");
        companyDataCache.setMaxEntriesLocalHeap(1000);
//        allAdminsCache.setTimeToLiveSeconds(20);
        
        CacheConfiguration allEmployeesCache = new CacheConfiguration();
        allEmployeesCache.setName("allEmployeesCache");
        allEmployeesCache.setMemoryStoreEvictionPolicy("LRU");
        allEmployeesCache.setMaxEntriesLocalHeap(1000);
//        allAdminsCache.setTimeToLiveSeconds(20);
        
        CacheConfiguration allDoctorsCache = new CacheConfiguration();
        allDoctorsCache.setName("allDoctorsCache");
        allDoctorsCache.setMemoryStoreEvictionPolicy("LRU");
        allDoctorsCache.setMaxEntriesLocalHeap(1000);
//        allAdminsCache.setTimeToLiveSeconds(20);
        
        
        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
        config.addCache(companyDataCache);
        config.addCache(allEmployeesCache);
        config.addCache(allDoctorsCache);

        return net.sf.ehcache.CacheManager.newInstance(config);
    }
    
    @Bean
    @Override
    public CacheManager cacheManager() {
        return new EhCacheCacheManager(ehCacheManager());
    }
    
}
