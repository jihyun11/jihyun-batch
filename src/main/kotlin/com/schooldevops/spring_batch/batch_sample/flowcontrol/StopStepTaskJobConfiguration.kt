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
class StopStepTaskJobConfiguration(
    private val transactionManager: PlatformTransactionManager,
) {
    private val log = KotlinLogging.logger {}

    @Bean(name = ["stepStop01"])
    fun stepStop01(jobRepository: JobRepository, transactionManager: PlatformTransactionManager): Step {
        log.info("------------------ Init myStep -----------------")

        return StepBuilder("stepStop01", jobRepository)
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

    @Bean(name = ["stepStop02"])
    fun stepStop02(
        jobRepository: JobRepository,
    ): Step {
        return StepBuilder("stepStop02", jobRepository)
            .tasklet({ _, _ ->
                log.info("Execute Step 02 Tasklet ...")
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }

    @Bean
    fun stopStepJob(
        stepStop01: Step,
        stepStop02: Step,
        jobRepository: JobRepository,
    ): Job {
        log.info("------------------ Init myJob -----------------")
        return JobBuilder(STOP_STEP_TASK, jobRepository)
            .incrementer(RunIdIncrementer())
            .start(stepStop01)
            .on("FAILED").stop()
            .from(stepStop01).on("COMPLETED").to(stepStop02)
            .end()
            .build()
    }

    companion object {
        const val STOP_STEP_TASK = "STOP_STEP_TASK"
    }
}