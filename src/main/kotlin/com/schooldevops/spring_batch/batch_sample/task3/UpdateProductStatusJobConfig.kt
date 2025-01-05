package com.schooldevops.spring_batch.batch_sample.task3

import mu.KotlinLogging
import org.apache.ibatis.session.SqlSessionFactory
import org.mybatis.spring.batch.MyBatisBatchItemWriter
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemReader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
class UpdateProductStatusJobConfig(
    private val dataSource: DataSource,
    private val sqlSessionFactory: SqlSessionFactory,
) {
    private val log = KotlinLogging.logger {}

    @Bean
    fun transactionManager(): DataSourceTransactionManager {
        return DataSourceTransactionManager(dataSource)
    }

    @Bean
    fun productStatusUpdateItemReader(): ItemReader<Product> {
        return MyBatisPagingItemReaderBuilder<Product>()
            .sqlSessionFactory(sqlSessionFactory)
            .queryId(SELECT_ID)
            .pageSize(CHUNK_SIZE)
            .build()
    }

    @Bean
    fun productStatusUpdateItemWriter(): MyBatisBatchItemWriter<Product> {
        return MyBatisBatchItemWriterBuilder<Product>()
            .sqlSessionFactory(sqlSessionFactory)
            .statementId(UPDATE_ID)
            .build()
    }

    @Bean
    fun updateProductStatusStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
    ): Step {
        log.info("------------------ Init updateProductStatusStep -----------------")
        return StepBuilder("updateProductStatusStep", jobRepository)
            .chunk<Product, Product>(CHUNK_SIZE, transactionManager)
            .reader(productStatusUpdateItemReader())
            .writer(productStatusUpdateItemWriter())
            .build()
    }

    @Bean
    fun updateProductStatusJob(
        updateProductStatusStep: Step,
        jobRepository: JobRepository,
    ): Job {
        log.info("------------------ Init updateProductStatusJob -----------------")
        return JobBuilder("updateProductStatusJob", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(updateProductStatusStep)
            .build()
    }

    companion object {
        const val CHUNK_SIZE = 50000
        const val UPDATE_ID =
            "com.schooldevops.spring_batch.batch_sample.task3.mapper.ProductMapper.updateStatusProduct"
        const val SELECT_ID =
            "com.schooldevops.spring_batch.batch_sample.task3.mapper.ProductMapper.findAll"
    }
}