package com.schooldevops.web.controller

import com.schooldevops.com.schooldevops.web.model.TestResponse
import com.schooldevops.spring.common.model.DataResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ApiController {
    @GetMapping("/test")
    fun test(
        @RequestParam id: Long,
    ): DataResponse<TestResponse> {
        return DataResponse.of(TestResponse(id))
    }
}