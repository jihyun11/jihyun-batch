package com.schooldevops.spring_batch.batch_sample.querydsl

import com.schooldevops.spring_batch.batch_sample.CustomItemProcessor
import com.schooldevops.spring_batch.batch_sample.Customer
import jakarta.persistence.EntityManagerFactory
import mu.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.file.FlatFileItemWriter
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.FileSystemResource
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class QuerydslPagingReaderJobConfig(
    private val entityManagerFactory: EntityManagerFactory,
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager
) {

    private val logger = KotlinLogging.logger {}

    @Bean
    fun customerQuerydslPagingItemReader(): QuerydslPagingItemReader<Customer> {
        return QuerydslPagingItemReaderBuilder<Customer>()
            .name("customerQuerydslPagingItemReader")
            .entityManagerFactory(entityManagerFactory)
            .chunkSize(CHUNK_SIZE)
            .querySupplier { jpaQueryFactory ->
                jpaQueryFactory.select(QCustomer.customer)
                    .from(QCustomer.customer)
                    .where(QCustomer.customer.age.gt(20))
            }
            .build()
    }

    @Bean
    fun customerQuerydslFlatFileItemWriter(): FlatFileItemWriter<Customer> {
        return FlatFileItemWriterBuilder<Customer>()
            .name("customerQuerydslFlatFileItemWriter")
            .resource(FileSystemResource("./output/customer_new_v2.csv"))
            .encoding(ENCODING)
            .delimited().delimiter("\t")
            .names("Name", "Age", "Gender")
            .build()
    }

    @Bean
    fun customerQuerydslPagingStep(): Step {
        logger.info("------------------ Init customerQuerydslPagingStep -----------------")

        return StepBuilder("customerJpaPagingStep", jobRepository)
            .chunk<Customer, Customer>(CHUNK_SIZE, transactionManager)
            .reader(customerQuerydslPagingItemReader())
            .processor(CustomItemProcessor())
            .writer(customerQuerydslFlatFileItemWriter())
            .build()
    }

    @Bean
    fun customerJpaPagingJob(customerQuerydslPagingStep: Step): Job {
        logger.info("------------------ Init customerJpaPagingJob -----------------")

        return JobBuilder(QUERYDSL_PAGING_CHUNK_JOB, jobRepository)
            .incrementer(RunIdIncrementer())
            .start(customerQuerydslPagingStep)
            .build()
    }

    companion object {
        const val CHUNK_SIZE = 2
        const val ENCODING = "UTF-8"
        const val QUERYDSL_PAGING_CHUNK_JOB = "QUERYDSL_PAGING_CHUNK_JOB"
    }
}
