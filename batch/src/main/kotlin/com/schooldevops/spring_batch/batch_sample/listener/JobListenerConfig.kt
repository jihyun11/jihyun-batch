package com.schooldevops.spring_batch.batch_sample.listener

import mu.KotlinLogging
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobExecutionListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JobListenerConfig {
    private val log = KotlinLogging.logger {}

    @Bean
    fun jobExecutionListener(): JobExecutionListener {
        return object : JobExecutionListener {
            override fun beforeJob(jobExecution: JobExecution) {
                log.info(" >>>>>> Before job: Job {} is starting...", jobExecution.jobInstance.jobName)
            }

            override fun afterJob(jobExecution: JobExecution) {
                log.info(" >>>>>> After job: Job {} is finished.", jobExecution.jobInstance.jobName)
            }
        }
    }
}