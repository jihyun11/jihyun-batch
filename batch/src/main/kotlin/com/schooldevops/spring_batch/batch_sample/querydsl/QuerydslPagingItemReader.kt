package com.schooldevops.spring_batch.batch_sample.querydsl

import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.item.database.AbstractPagingItemReader
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Function

class QuerydslPagingItemReader<T>(
    name: String,
    entityManagerFactory: EntityManagerFactory,
    private val querySupplier: Function<JPAQueryFactory, JPAQuery<T>>,
    chunkSize: Int,
    private val alwaysReadFromZero: Boolean
) : AbstractPagingItemReader<T>() {

    private val em: EntityManager = entityManagerFactory.createEntityManager()

    init {
        setPageSize(chunkSize)
        setName(name)
    }

    override fun doClose() {
        em.close()
        super.doClose()
    }

    override fun doReadPage() {
        initQueryResult()

        val jpaQueryFactory = JPAQueryFactory(em)
        val offset = if (alwaysReadFromZero) 0L else page * pageSize.toLong()

        val query = querySupplier.apply(jpaQueryFactory)
            .offset(offset)
            .limit(pageSize.toLong())

        val queryResult = query.fetch()
        for (entity in queryResult) {
            em.detach(entity)
            results.add(entity)
        }
    }

    private fun initQueryResult() {
        if (results == null) {
            results = CopyOnWriteArrayList()
        } else {
            results.clear()
        }
    }
}

