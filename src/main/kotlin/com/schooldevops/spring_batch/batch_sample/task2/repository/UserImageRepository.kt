package com.schooldevops.spring_batch.batch_sample.task2.repository

import com.schooldevops.spring_batch.batch_sample.task2.entity.UserImageEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserImageRepository : JpaRepository<UserImageEntity, Long>