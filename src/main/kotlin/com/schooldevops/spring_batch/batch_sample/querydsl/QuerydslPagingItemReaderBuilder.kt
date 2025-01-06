package com.schooldevops.spring_batch.batch_sample.querydsl

import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManagerFactory
import org.springframework.util.ClassUtils

class QuerydslPagingItemReaderBuilder<T> {
    private var entityManagerFactory: EntityManagerFactory? = null
    private var querySupplier: ((JPAQueryFactory) -> JPAQuery<T>)? = null
    private var chunkSize: Int = 10
    private var name: String? = null
    private var alwaysReadFromZero: Boolean? = null

    fun entityManagerFactory(entityManagerFactory: EntityManagerFactory) = apply {
        this.entityManagerFactory = entityManagerFactory
    }

    fun querySupplier(querySupplier: (JPAQueryFactory) -> JPAQuery<T>) = apply {
        this.querySupplier = querySupplier
    }

    fun chunkSize(chunkSize: Int) = apply {
        this.chunkSize = chunkSize
    }

    fun name(name: String) = apply {
        this.name = name
    }

    fun alwaysReadFromZero(alwaysReadFromZero: Boolean) = apply {
        this.alwaysReadFromZero = alwaysReadFromZero
    }

    fun build(): QuerydslPagingItemReader<T> {
        val resolvedName = name ?: ClassUtils.getShortName(QuerydslPagingItemReader::class.java)
        val resolvedEntityManagerFactory = entityManagerFactory
            ?: throw IllegalArgumentException("EntityManagerFactory cannot be null!")
        val resolvedQuerySupplier = querySupplier
            ?: throw IllegalArgumentException("Function<JPAQueryFactory, JPAQuery<T>> cannot be null!")
        val resolvedAlwaysReadFromZero = alwaysReadFromZero ?: false

        return QuerydslPagingItemReader(
            name = resolvedName,
            entityManagerFactory = resolvedEntityManagerFactory,
            querySupplier = resolvedQuerySupplier,
            chunkSize = chunkSize,
            alwaysReadFromZero = resolvedAlwaysReadFromZero
        )
    }
}
