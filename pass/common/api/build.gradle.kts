plugins {
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    compileOnly(libs.compose.stable.marker)

    api(libs.core.network.domain)
    api(libs.core.utilKotlin)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
}
