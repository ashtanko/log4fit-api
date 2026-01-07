package dev.shtanko.config

data class RootConfig(
    val ktor: KtorConfig,
    val app: AppConfig
)

data class KtorConfig(
    val deployment: DeploymentConfig
)

data class DeploymentConfig(
    val port: Int
)

data class AppConfig(
    val env: String,
    val database: DatabaseConfig,
    val jwt: JwtConfig
)

sealed class DatabaseConfig {
    data class H2(val type: String) : DatabaseConfig()
    data class Postgres(
        val type: String,
        val driver: String,
        val url: String,
        val user: String,
        val password: String
    ) : DatabaseConfig()
}

data class JwtConfig(
    val domain: String,
    val issuer: String,
    val audience: String,
    val realm: String
)

//fun loadConfig(): RootConfig {
//    val env = System.getenv("KTOR_ENV") ?: "development"
//    return ConfigLoaderBuilder.default()
//        .addResourceSource("/application.conf", optional = true)
//        .addResourceSource("/$env.conf")
//        .build()
//        .loadConfigOrThrow<RootConfig>("/")
//}
