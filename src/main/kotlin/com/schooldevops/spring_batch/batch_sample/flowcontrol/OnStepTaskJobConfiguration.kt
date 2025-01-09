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
class OnStepTaskJobConfiguration(
    private val transactionManager: PlatformTransactionManager,
) {
    private val log = KotlinLogging.logger {}

    @Bean(name = ["stepOn01"])
    fun stepOn01(
        jobRepository: JobRepository,
    ): Step {
        log.info("------------------ Init myStep -----------------")
        return StepBuilder("stepOn01", jobRepository)
            .tasklet({ _, _ ->
                log.info("Execute Step 01 Tasklet ...")

                val randomValue = (0 until 1000).random()

                if (randomValue % 2 == 0) {
                    RepeatStatus.FINISHED
                } else {
                    throw RuntimeException("Error This value is Odd: $randomValue")
                }
            }, transactionManager)
            .build()
    }

    @Bean(name = ["stepOn02"])
    fun step02(
        jobRepository: JobRepository,
    ): Step {
        log.info("------------------ Init myStep -----------------")
        return StepBuilder("stepOn02", jobRepository)
            .tasklet({ _, _ ->
                log.info("Execute Step 02 Tasklet ...")
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }

    @Bean(name = ["stepOn03"])
    fun step03(
        jobRepository: JobRepository,
    ): Step {
        log.info("------------------ Init myStep -----------------")
        return StepBuilder("stepOn03", jobRepository)
            .tasklet({ _, _ ->
                log.info("Execute Step 03 Tasklet ...")
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }

    @Bean
    fun onStepTaskJob(
        stepOn01: Step,
        stepOn02: Step,
        stepOn03: Step,
        jobRepository: JobRepository,
    ): Job {
        log.info("------------------ Init myJob -----------------")
        return JobBuilder(ON_STEP_TASK, jobRepository)
            .incrementer(RunIdIncrementer())
            .start(stepOn01)
            .on("FAILED").to(stepOn03)
            .from(stepOn01).on("COMPLETED").to(stepOn02)
            .end()
            .build()
    }

    companion object {
        const val ON_STEP_TASK = "ON_STEP_TASK"
    }
}