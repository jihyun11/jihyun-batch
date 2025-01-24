package common.model

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.ZonedDateTime

open class BaseResponse<E : Enum<*>> {

    // 요청 성공, 실패 여부
    var success: Boolean = true

    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    // 응답 일시
    var timestamp: ZonedDateTime = ZonedDateTime.now()

    // 응답 메시지
    var message: String? = null

    // 에러 코드
    var errorCode: E? = null

    // 에러 메시지
    var errorMessage: String? = null
}

enum class ErrorCode {
    EXPECTED_EXCEPTION,
    EXCEPTION,
}