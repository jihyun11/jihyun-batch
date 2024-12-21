package com.schooldevops.spring_batch.batch_sample.lib

import io.kotest.core.spec.style.BehaviorSpec
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import java.sql.PreparedStatement
import kotlin.random.Random

@SpringBootTest
@SpringBatchTest
class CreateUserAndImage(
    @Autowired private val jdbcTemplate: JdbcTemplate,
) : BehaviorSpec(
    {
        Given("데이터베이스에 회원 5만명과 이미지 50만개를 생성한다.") {
            val userSql = "INSERT INTO \"user\" (name, status) VALUES (?, ?)"
            val userData = (1..50000).map {
                "김지현$it"
            }

            jdbcTemplate.batchUpdate(userSql, userData, 1000) { ps: PreparedStatement, name: String ->
                ps.setString(1, name)
                ps.setString(2, if (Random.nextBoolean()) "ACTIVE" else "INACTIVE")
            }

            val imageSql = "INSERT INTO user_image (user_id) VALUES (?)"
            val imageData = (1..500000).map {
                Random.nextInt(1, 50000).toLong()
            }

            jdbcTemplate.batchUpdate(imageSql, imageData, 1000) { ps: PreparedStatement, userId: Long ->
                ps.setLong(1, userId)
            }
        }
    }
)