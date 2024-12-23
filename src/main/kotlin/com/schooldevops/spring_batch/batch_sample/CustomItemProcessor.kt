package com.schooldevops.spring_batch.batch_sample

import mu.KotlinLogging
import org.springframework.batch.item.ItemProcessor

class CustomItemProcessor : ItemProcessor<Customer, Customer> {

    private val log = KotlinLogging.logger {}

    override fun process(item: Customer): Customer {
        log.info("Item Processor ------------------- {}", item)
        return item
    }
}