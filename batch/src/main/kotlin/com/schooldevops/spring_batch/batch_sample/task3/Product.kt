package com.schooldevops.spring_batch.batch_sample.task3

import java.time.LocalDate

data class Product(
    val id: Long,
    val name: String,
    val status: String,
    val date: LocalDate,
)