package com.schooldevops.spring_batch.batch_sample.flowcontrol

import mu.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class NextStepTaskJobConfiguration(
    private val transactionManager: PlatformTransactionManager,
) {
    private val log = KotlinLogging.logger {}

    @Bean(name = ["step01"])
    fun step01(
        jobRepository: JobRepository,
    ): Step {
        log.info("------------------ Init myStep -----------------")
        return StepBuilder("step01", jobRepository)
            .tasklet({ _, _ ->
                log.info("Execute Step 01 Tasklet ...")
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }

    @Bean(name = ["step02"])
    fun step02(
        jobRepository: JobRepository,
    ): Step {
        log.info("------------------ Init myStep -----------------")
        return StepBuilder("step02", jobRepository)
            .tasklet({ _, _ ->
                log.info("Execute Step 02 Tasklet ...")
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }

    @Bean
    fun nextStepJob(
        step01: Step,
        step02: Step,
        jobRepository: JobRepository,
    ): Job {
        log.info("------------------ Init myJob -----------------")
        return JobBuilder(NEXT_STEP_TASK, jobRepository)
            .incrementer(RunIdIncrementer())
            .start(step01)
            .next(step02)
            .build()
    }

    companion object {
        const val NEXT_STEP_TASK = "NEXT_STEP_TASK"
    }

}