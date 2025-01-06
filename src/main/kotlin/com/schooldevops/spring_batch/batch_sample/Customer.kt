package com.schooldevops.spring_batch.batch_sample

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class Customer(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    var name: String,
    var age: Int,
    var gender: String,
)