package com.schooldevops.spring_batch.batch_sample

import org.springframework.batch.item.ItemProcessor
import java.util.concurrent.ConcurrentHashMap

class AggregateCustomerProcessor(
    private val aggregateCustomers: ConcurrentHashMap<String, Int>,
) : ItemProcessor<Customer, Customer> {
    override fun process(item: Customer): Customer {
        aggregateCustomers.putIfAbsent("TOTAL_CUSTOMER", 0)
        aggregateCustomers.putIfAbsent("TOTAL_AGES", 0)
        aggregateCustomers["TOTAL_CUSTOMER"] = aggregateCustomers["TOTAL_CUSTOMER"]!! + 1
        aggregateCustomers["TOTAL_AGES"] = aggregateCustomers["TOTAL_AGES"]!! + item.age
        return item
    }
}