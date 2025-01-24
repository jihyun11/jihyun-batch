package com.schooldevops.spring_batch.batch_sample.querydsl

import mu.KotlinLogging
import org.springframework.batch.item.ItemProcessor


class CustomItemProcessor : ItemProcessor<CustomerEntity, CustomerEntity> {

    private val log = KotlinLogging.logger {}

    override fun process(item: CustomerEntity): CustomerEntity {
        log.info("Item Processor ------------------- {}", item)
        return item
    }
}