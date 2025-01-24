package com.schooldevops.spring_batch.batch_sample.task2

import com.schooldevops.spring_batch.batch_sample.task2.entity.UserEntity
import com.schooldevops.spring_batch.batch_sample.task2.repository.UserRepository
import jakarta.persistence.EntityManagerFactory
import mu.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JpaCursorItemReader
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.transaction.PlatformTransactionManager


@Configuration
class DeleteUserImageJobConfig(
    private val entityManagerFactory: EntityManagerFactory,
    private val userRepository: UserRepository,
) {
    private val log = KotlinLogging.logger {}

    /**
     * cascade delete 관계니까 user 테이블에서만 지워도 됨
     */
    @Bean
    fun transactionManager(): PlatformTransactionManager {
        return JpaTransactionManager(entityManagerFactory)
    }

    @Bean
    fun deleteInactiveUserItemReader(): JpaCursorItemReader<UserEntity> {
        transactionManager() // 주석 달아놓은 함수 호출해도 여기에서 주석 보임!! (여러줄 주석만 됨)
        return JpaCursorItemReaderBuilder<UserEntity>()
            .name("deleteInactiveUserItemReader")
            .queryString("SELECT u FROM UserEntity u WHERE u.status = 'INACTIVE'")
            .entityManagerFactory(entityManagerFactory)
            .build()
    }

    @Bean
    fun deleteInactiveUserItemProcessor(): ItemProcessor<UserEntity, UserEntity> {
        return ItemProcessor { user ->
            user
        }
    }

    @Bean
    fun deleteInactiveUserItemWriter(): ItemWriter<UserEntity> {
        return ItemWriter { users ->
            users.forEach { user ->
                userRepository.delete(user)
            }
            userRepository.flush()
        }
    }

    @Bean
    fun deleteInactiveUserStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
    ): Step {
        log.info("------------------ Init deleteInactiveUserStep -----------------")

        return StepBuilder("deleteInactiveUserStep", jobRepository)
            .chunk<UserEntity, UserEntity>(CHUNK_SIZE, transactionManager)
            .reader(deleteInactiveUserItemReader())
            .processor(deleteInactiveUserItemProcessor())
            .writer(deleteInactiveUserItemWriter())
            .build()
    }

    @Bean
    fun deleteInactiveUserJob(
        deleteInactiveUserStep: Step,
        jobRepository: JobRepository
    ): Job {
        log.info("------------------ Init deleteInactiveUserJob -----------------")

        return JobBuilder(DELETE_USER_IMAGE_JOB, jobRepository)
            .incrementer(RunIdIncrementer())
            .start(deleteInactiveUserStep)
            .build()
    }

    companion object {
        const val CHUNK_SIZE = 1000
        const val DELETE_USER_IMAGE_JOB = "DELETE_USER_IMAGE_JOB"
    }
}