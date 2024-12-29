package com.schooldevops.spring_batch.batch_sample.composite

import com.schooldevops.spring_batch.batch_sample.Customer
import mu.KotlinLogging
import org.apache.ibatis.session.SqlSessionFactory
import org.mybatis.spring.batch.MyBatisPagingItemReader
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.file.FlatFileItemWriter
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder
import org.springframework.batch.item.support.CompositeItemProcessor
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.FileSystemResource
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class MyBatisReaderJobConfig(
    private val sqlSessionFactory: SqlSessionFactory,
) {

    val log = KotlinLogging.logger {}

    @Bean
    fun myBatisItemReader(): MyBatisPagingItemReader<Customer> {
        return MyBatisPagingItemReaderBuilder<Customer>()
            .sqlSessionFactory(sqlSessionFactory)
            .pageSize(CHUNK_SIZE)
            .queryId("com.schooldevops.springbatch.batchsample.jobs.selectCustomers")
            .build()
    }

    @Bean
    fun customerCursorFlatFileItemWriter(): FlatFileItemWriter<Customer> {
        return FlatFileItemWriterBuilder<Customer>()
            .name("customerCursorFlatFileItemWriter")
            .resource(FileSystemResource("./output/customer_new_v4.csv"))
            .encoding(ENCODING)
            .delimited().delimiter("\t")
            .names("Name", "Age", "Gender")
            .build()
    }

    @Bean
    fun compositeItemProcessor(): CompositeItemProcessor<Customer, Customer> {
        return CompositeItemProcessorBuilder<Customer, Customer>()
            .delegates(
                listOf(
                    LowerCaseItemProcessor(),
                    After20YearsItemProcessor()
                )
            )
            .build()
    }

    @Bean
    fun customerJdbcCursorStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager
    ): Step {
        log.info("------------------ Init customerJdbcCursorStep -----------------")

        return StepBuilder("customerJdbcCursorStep", jobRepository)
            .chunk<Customer, Customer>(CHUNK_SIZE, transactionManager)
            .reader(myBatisItemReader())
            .processor(compositeItemProcessor())
            .writer(customerCursorFlatFileItemWriter())
            .build()
    }

    @Bean
    fun customerJdbcCursorPagingJob(
        customerJdbcCursorStep: Step,
        jobRepository: JobRepository
    ): Job {
        log.info("------------------ Init customerJdbcCursorPagingJob -----------------")

        return JobBuilder(MYBATIS_CHUNK_JOB, jobRepository)
            .incrementer(RunIdIncrementer())
            .start(customerJdbcCursorStep)
            .build()
    }

    companion object {
        const val CHUNK_SIZE = 2
        const val ENCODING = "UTF-8"
        const val MYBATIS_CHUNK_JOB = "MYBATIS_CHUNK_JOB"
    }
}