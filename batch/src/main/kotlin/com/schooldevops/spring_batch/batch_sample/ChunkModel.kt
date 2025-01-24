package com.schooldevops.spring_batch.batch_sample

import org.springframework.batch.item.ItemProcessor

class MyItemProcessor : ItemProcessor<MyInputObject, MyOutputObject> {
    override fun process(item: MyInputObject): MyOutputObject {
        return MyOutputObject(
            id = item.id,
            name = item.name,
        )
    }
}

data class MyInputObject(
    val id: Long,
    val name: String,
)

data class MyOutputObject(
    val id: Long,
    val name: String,
)