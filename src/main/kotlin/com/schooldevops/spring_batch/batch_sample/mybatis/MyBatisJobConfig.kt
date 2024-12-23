package com.schooldevops.spring_batch.batch_sample.mybatis

import com.schooldevops.spring_batch.batch_sample.CustomItemProcessor
import com.schooldevops.spring_batch.batch_sample.Customer
import mu.KotlinLogging
import org.apache.ibatis.session.SqlSessionFactory
import org.mybatis.spring.batch.MyBatisBatchItemWriter
import org.mybatis.spring.batch.MyBatisPagingItemReader
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder
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
class MyBatisJobConfig(
    private val sqlSessionFactory: SqlSessionFactory,
) {
    val log = KotlinLogging.logger {}

    @Bean
    fun myBatisItemReader(): MyBatisPagingItemReader<Customer> {
        return MyBatisPagingItemReaderBuilder<Customer>()
            .sqlSessionFactory(sqlSessionFactory)
            .pageSize(CHUNK_SIZE)
            .queryId(QUERY_ID)
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
    fun mybatisItemWriter(): MyBatisBatchItemWriter<Customer> {
        return MyBatisBatchItemWriterBuilder<Customer>()
            .sqlSessionFactory(sqlSessionFactory)
            .statementId(STATEMENT_ID)
            .build()
    }

    @Bean
    fun customerJdbcCursorStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
    ): Step {
        log.info("------------------ Init customerJdbcCursorStep -----------------")
        return StepBuilder("customerJdbcCursorStep", jobRepository)
            .chunk<Customer, Customer>(CHUNK_SIZE, transactionManager)
            .reader(myBatisItemReader())
            .writer(mybatisItemWriter())
            .processor(CustomItemProcessor())
            .build()
    }

    @Bean
    fun customerJdbcCursorPagingJob(
        customerJdbcCursorStep: Step,
        jobRepository: JobRepository,
    ): Job {
        log.info("------------------ Init customerJdbcCursorJob -----------------")
        return JobBuilder(MYBATIS_CHUNK_JOB, jobRepository)
            .incrementer(RunIdIncrementer())
            .start(customerJdbcCursorStep)
            .build()

    }

    companion object {
        const val CHUNK_SIZE = 100

        // TODO 이거 경로 고치기 (customer.xml 파일도 같이)
        const val QUERY_ID = "com.schooldevops.springbatch.batchsample.jobs.selectCustomers"
        const val STATEMENT_ID = "com.schooldevops.springbatch.batchsample.jobs.insertCustomers"
        const val ENCODING = "UTF-8"
        const val MYBATIS_CHUNK_JOB = "MYBATIS_CHUNK_JOB"
    }
}