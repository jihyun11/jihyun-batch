package com.schooldevops.spring_batch.batch_sample

import org.springframework.batch.item.file.transform.LineAggregator

class CustomerLineAggregator : LineAggregator<Customer> {
    override fun aggregate(item: Customer): String {
        return item.name + "," + item.age
    }
}