package com.schooldevops.spring_batch.batch_sample.task2.repository

import com.schooldevops.spring_batch.batch_sample.task2.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, Long>