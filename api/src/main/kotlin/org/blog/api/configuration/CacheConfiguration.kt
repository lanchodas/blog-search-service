package org.blog.api.configuration

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
@EnableCaching
class CacheConfiguration {
    /**
     * CaffeineCacheManager를 등록합니다.
     * Caffeine의 refreshAfterWrite를 같이 적용하려면 LoadingCache(refresh 하기 위한 로딩 메서드 연결)를
     * 사용해야하는데, 이 부분은 편의상 생략합니다. refreshAfterWrite를 expireAfterWrite와 같이 사용하게 되면
     * 캐시 만료가 되기전 미리 새로운 데이터를 로딩해 캐싱할 수 있어서 유용합니다.
     */
    @Bean
    fun cacheManager(): CacheManager {
        val caffeine = Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(Duration.ofSeconds(10))
            // .refreshAfterWrite(Duration.ofSeconds(8))

        return CaffeineCacheManager().apply {
            setCaffeine(caffeine)
        }
    }
}
