package org.blog.client

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class KakaoClient(
    private val restClient: RestClient
) {

    private val log: Logger = LoggerFactory.getLogger(KakaoClient::class.java)

    fun search() {
        val result = restClient.get()
            .uri("/v2/search/blog")
            .retrieve()
            .onStatus({ it.value() != 200 }, { request, response ->
                throw RuntimeException("카카오 블로그 검색 에러")
            })
            .body(String::class.java)

        log.info(result)
    }
}
