package com.schooldevops.spring_batch.batch_sample.task3.mapper

import com.schooldevops.spring_batch.batch_sample.task3.Product
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update

@Mapper
interface ProductMapper {
    @Update(
        """
        UPDATE product
        SET status = 'PURCHASE_CONFIRM'
        WHERE "date" < CURRENT_DATE - INTERVAL '3 MONTH'
    """
    )
    fun updateStatusProduct()

    @Select(
        """
        SELECT id, name, status, "date"
        FROM product
    """
    )
    fun findAll(): List<Product>
}