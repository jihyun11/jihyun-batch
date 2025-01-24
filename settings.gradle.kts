// JDK 버전 자동으로 다운받아 주는 플러그인
// 근데 17버전 쓰는지 명시는 어디서 함?
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

rootProject.name = "batch-sample"
include(
    "jihyun",
    "batch"
)
