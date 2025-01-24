package com.schooldevops.spring_batch.batch_sample.listener

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecutionListener
import org.springframework.batch.core.Step
import org.springframework.batch.core.StepExecutionListener
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class NextStepTaskJobConfigurationListener {
    @Bean(name = ["step01"])
    fun step01(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        stepExecutionListener: StepExecutionListener
    ): Step {
        return StepBuilder("step01", jobRepository)
            .tasklet({ _, _ -> RepeatStatus.FINISHED }, transactionManager)
            .listener(stepExecutionListener)
            .build()
    }

    @Bean(name = ["step02"])
    fun step02(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        stepExecutionListener: StepExecutionListener
    ): Step {
        return StepBuilder("step02", jobRepository)
            .tasklet({ _, _ -> RepeatStatus.FINISHED }, transactionManager)
            .listener(stepExecutionListener)
            .build()
    }

    @Bean
    fun nextStepJob(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        step01: Step,
        step02: Step,
        jobExecutionListener: JobExecutionListener
    ): Job {
        return JobBuilder(NEXT_STEP_TASK, jobRepository)
            .start(step01)
            .next(step02)
            .listener(jobExecutionListener)
            .build()
    }

    companion object {
        const val NEXT_STEP_TASK  = "NEXT_STEP_TASK"
    }
}