package com.schooldevops.spring.common.model

data class DataResponse<T>(
    val data: T,
) : CommonResponse() {
    companion object {
        fun <T> of(data: T): DataResponse<T> {
            return DataResponse(data)
        }
    }
}