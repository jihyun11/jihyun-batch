package com.schooldevops.spring_batch.batch_sample.listener

import mu.KotlinLogging
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.StepExecutionListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StepListenerConfig {
    private val log = KotlinLogging.logger {}

    @Bean
    fun stepExecutionListener(): StepExecutionListener {
        return object : StepExecutionListener {
            override fun beforeStep(stepExecution: StepExecution) {
                log.info("------ Before Step: Step {} is starting...", stepExecution.stepName)
            }

            override fun afterStep(stepExecution: StepExecution): ExitStatus {
                log.info("------ After Step: Step {} is finished.", stepExecution.stepName)
                return stepExecution.exitStatus
            }
        }
    }
}