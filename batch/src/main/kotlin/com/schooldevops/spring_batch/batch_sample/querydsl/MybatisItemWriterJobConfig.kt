package com.schooldevops.spring_batch.batch_sample.querydsl

import com.schooldevops.spring_batch.batch_sample.Customer
import mu.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class MybatisItemWriterJobConfig(
    private val customItemWriter: CustomItemWriter
) {

    private val logger = KotlinLogging.logger {}

    @Bean
    fun flatFileItemReader(): FlatFileItemReader<Customer> {
        return FlatFileItemReaderBuilder<Customer>()
            .name("FlatFileItemReader")
            .resource(ClassPathResource("./customer.csv"))
            .encoding(ENCODING)
            .delimited()
            .delimiter(",")
            .names("name", "age", "gender")
            .targetType(Customer::class.java)
            .build()
    }

    @Bean
    fun flatFileStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager
    ): Step {
        logger.info { "------------------ Init flatFileStep -----------------" }
        return StepBuilder("flatFileStep", jobRepository)
            .chunk<Customer, Customer>(CHUNK_SIZE, transactionManager)
            .reader(flatFileItemReader())
            .writer(customItemWriter)
            .build()
    }

    @Bean
    fun flatFileJob(
        flatFileStep: Step,
        jobRepository: JobRepository
    ): Job {
        logger.info { "------------------ Init flatFileJob -----------------" }
        return JobBuilder(MY_BATIS_ITEM_WRITER, jobRepository)
            .incrementer(RunIdIncrementer())
            .start(flatFileStep)
            .build()
    }

    companion object {
        const val CHUNK_SIZE = 100
        const val ENCODING = "UTF-8"
        const val MY_BATIS_ITEM_WRITER = "MY_BATIS_ITEM_WRITER"
    }
}
