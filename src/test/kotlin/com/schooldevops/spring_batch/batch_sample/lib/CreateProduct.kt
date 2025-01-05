package com.schooldevops.spring_batch.batch_sample.lib

import io.kotest.core.spec.style.BehaviorSpec
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import java.sql.PreparedStatement
import java.time.LocalDate
import kotlin.random.Random

@SpringBootTest
@SpringBatchTest
class CreateProduct(
    @Autowired private val jdbcTemplate: JdbcTemplate,
) : BehaviorSpec(
    {
        Given("상품 50,000개 생성하기.") {
            val productSql = "INSERT INTO product (name, status, date) VALUES (?, ?, ?)"
            val userData = (1..50000).map {
                "상품$it"
            }

            val startDate = LocalDate.of(2024, 6, 1)
            val endDate = LocalDate.of(2025, 1, 1)
            val daysBetween = endDate.toEpochDay() - startDate.toEpochDay()

            jdbcTemplate.batchUpdate(productSql, userData, 1000) { ps: PreparedStatement, name: String ->
                ps.setString(1, name)
                ps.setString(2, "DELIVERY_COMPLETE")
                val randomDay = startDate.plusDays(Random.nextLong(0, daysBetween))
                ps.setDate(3, java.sql.Date.valueOf(randomDay))
            }
        }
    }
)