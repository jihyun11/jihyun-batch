package com.schooldevops.spring_batch.batch_sample.querydsl

import com.schooldevops.spring_batch.batch_sample.Customer
import mu.KotlinLogging
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class CustomItemWriter(
    private val customService: CustomService,
) : ItemWriter<Customer> {

    override fun write(chunk: Chunk<out Customer>) {
        logger.info { "Call Porcess in CustomItemWriter..." }
        for (customer in chunk) {
            customService.processToOtherService(customer)
        }
    }
}