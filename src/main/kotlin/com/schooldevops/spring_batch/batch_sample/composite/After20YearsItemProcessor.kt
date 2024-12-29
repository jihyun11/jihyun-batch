package com.schooldevops.spring_batch.batch_sample.composite

import com.schooldevops.spring_batch.batch_sample.Customer
import org.springframework.batch.item.ItemProcessor

class After20YearsItemProcessor : ItemProcessor<Customer, Customer> {
    override fun process(item: Customer): Customer? {
        item.age += 20
        return item
    }
}