package com.schooldevops.spring_batch.batch_sample

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

//@EnableBatchProcessing
@SpringBootApplication
class BatchSampleApplication

fun main(args: Array<String>) {
	runApplication<BatchSampleApplication>(*args)
}
