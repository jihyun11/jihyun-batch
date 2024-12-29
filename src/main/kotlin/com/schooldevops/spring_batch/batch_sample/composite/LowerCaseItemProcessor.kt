package com.schooldevops.spring_batch.batch_sample.composite

import com.schooldevops.spring_batch.batch_sample.Customer
import org.springframework.batch.item.ItemProcessor

class LowerCaseItemProcessor : ItemProcessor<Customer, Customer> {
    override fun process(item: Customer): Customer {
        item.name = item.name.toLowerCase()
        item.gender = item.gender.lowercase()
        return item
    }
}