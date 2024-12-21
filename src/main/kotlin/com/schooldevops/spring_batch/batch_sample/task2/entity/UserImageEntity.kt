package com.schooldevops.spring_batch.batch_sample.task2.entity

import jakarta.persistence.*

@Entity
@Table(name = "user_image")
class UserImageEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val userId: Long,
)