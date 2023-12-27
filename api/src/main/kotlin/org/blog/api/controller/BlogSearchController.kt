package org.blog.api.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class BlogSearchController {

    @GetMapping("/search")
    fun search(): String {
        return "Hello, /search"
    }

    @GetMapping("/popular-search-keywords")
    fun popularSearchKeywords(): String {
        return "Hello, /popular-search-keywords"
    }
}
