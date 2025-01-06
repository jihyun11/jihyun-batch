package com.schooldevops.spring_batch.batch_sample.querydsl

import com.schooldevops.spring_batch.batch_sample.Customer
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class CustomService {

    private val logger = KotlinLogging.logger {}


    fun processToOtherService(item: Customer): Map<String, String> {
        logger.info("Call API to OtherService....")

        return mapOf("code" to "200", "message" to "OK")
    }
}