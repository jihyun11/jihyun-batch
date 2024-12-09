package com.schooldevops.spring_batch.batch_sample

import mu.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.database.JdbcPagingItemReader
import org.springframework.batch.item.database.Order
import org.springframework.batch.item.database.PagingQueryProvider
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean
import org.springframework.batch.item.file.FlatFileItemWriter
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.FileSystemResource
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource


@Configuration
class JdbcPagingReaderJobConfig(
    private val dataSource: DataSource,
) {
    private val log = KotlinLogging.logger {}

    @Bean
    fun queryProvider(): PagingQueryProvider {
        val queryProvider = SqlPagingQueryProviderFactoryBean()
        queryProvider.setDataSource(dataSource)
        queryProvider.setSelectClause("id, name, age, gender")
        queryProvider.setFromClause("from customer")
        queryProvider.setWhereClause("where age >= :age")

        val sortKeys = mapOf("id" to Order.DESCENDING)
        queryProvider.setSortKeys(sortKeys)

        return queryProvider.`object`
    }

    @Bean
    fun jdbcPagingItemReader(): JdbcPagingItemReader<Customer> {
        val parameterValue = mapOf("age" to 20)

        return JdbcPagingItemReaderBuilder<Customer>()
            .name("jdbcPagingItemReader")
            .fetchSize(CHUNK_SIZE)
            .dataSource(dataSource)
            .rowMapper(BeanPropertyRowMapper(Customer::class.java))
            .queryProvider(queryProvider())
            .parameterValues(parameterValue)
            .build()
    }

    @Bean
    fun customerFlatFileItemWriter(): FlatFileItemWriter<Customer> {
        return FlatFileItemWriterBuilder<Customer>()
            .name("customerFlatFileItemWriter")
            .resource(FileSystemResource("./output/customer_new_v1.csv"))
            .encoding(ENCODING)
            .delimited()
            .delimiter("\t")
            .names("name", "age", "gender")
            .build()
    }

    @Bean
    fun customerJdbcPagingStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
    ): Step {
        log.info("------------------ Init customerJdbcPagingStep -----------------")

        return StepBuilder("customerJdbcPagingStep", jobRepository)
            .chunk<Customer, Customer>(CHUNK_SIZE, transactionManager)
            .reader(jdbcPagingItemReader())
            .writer(customerFlatFileItemWriter())
            .build()
    }

    @Bean
    fun customerJdbcPagingJob(
        customerJdbcPagingStep: Step,
        jobRepository: JobRepository
    ): Job {
        log.info("------------------ Init customerJdbcPagingJob -----------------")

        return JobBuilder(JDBC_PAGING_CHUNK_JOB, jobRepository)
            .incrementer(RunIdIncrementer())
            .start(customerJdbcPagingStep)
            .build()
    }

    companion object {
        const val CHUNK_SIZE = 2
        const val ENCODING = "UTF-8"
        const val JDBC_PAGING_CHUNK_JOB = "JDBC_PAGING_CHUNK_JOB"
    }
}