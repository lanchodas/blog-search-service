package org.blog.api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.blog.api.request.BlogSearchRequest
import org.blog.api.service.KeywordCountService
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

/**
 * 검색 키워드와 검색 횟수를 저장하는 Map입니다.
 * 파일내에서만 접근 가능하며, 검색 횟수 저장의 동시성 문제를 위해 사용했습니다.
 *
 * (서버가 여러 대 동작하겠지만) 한 서버는 자신의 KEYWORD_COUNT_MAP으로 검색 횟수를 가지고 있다가,
 * 주기적으로 데이터베이스에 저장하고 KEYWORD_COUNT_MAP에서는 제거합니다.
 *
 * 100% 실시간은 아니지만 DB의 부하, 검색 횟수의 중요도 등을 고려했을 때 준 실시간으로 처리하는게
 * 외부 시스템 도움없이 구현하기에는 적절하다고 판단했습니다.
 */
internal val KEYWORD_COUNT_MAP = ConcurrentHashMap<String, Long>()

/**
 * Spring AOP로 구현할 수도 있지만, 간단한 로직은 이런 Trailing Lambda 형태로 구현하는 것도
 * 괜찮은 것 같아 이렇게 구현했습니다.
 * KEYWORD_COUNT_MAP에 검색 키워드가 존재하지 않으면 1로 초기화하고, 존재하면 1을 더합니다.
 * 원래 필요한 로직인 블로그 검색 기능과 별도로 동작하게 하기 위해 코루틴 사용했습니다.
 */
fun <T> incrementKeywordCount(request: BlogSearchRequest, function: () -> T): T {
    // page가 2 이상인 것은 새로운 검색이라고 간주하지 않기 때문에 page = 1일 때만 카운트를 증가시킵니다.
    if (request.page == 1) {
        CoroutineScope(Dispatchers.IO).launch {
            KEYWORD_COUNT_MAP.compute(request.query) { _, count ->
                count?.plus(1) ?: 1
            }
        }
    }

    return function.invoke()
}

@Component
@EnableScheduling
class ScheduledTask(private val keywordCountService: KeywordCountService) {
    /**
     * 주기적으로(직전 작업 끝난 후 5초 뒤) KEYWORD_COUNT_MAP에 저장된
     * 검색 키워드 카운트를 데이터베이스에 저장하고 KEYWORD_COUNT_MAP에서는 제거합니다.
     */
    @Scheduled(fixedDelay = 5000)
    fun upsertKeywordCount() {
        val iterator = KEYWORD_COUNT_MAP.iterator()
        while (iterator.hasNext()) {
            val (keyword, count) = iterator.next()

            keywordCountService.upsert(keyword, count)

            iterator.remove()
        }
    }
}
