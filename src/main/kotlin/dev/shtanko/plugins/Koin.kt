package dev.shtanko.plugins

import dev.shtanko.repository.TokenRepository
import dev.shtanko.repository.UserRepository
import dev.shtanko.repository.impl.TokenRepositoryImpl
import dev.shtanko.repository.impl.UserRepositoryImpl
import dev.shtanko.service.AuthService
import dev.shtanko.service.JwtService
import dev.shtanko.service.TransactionService
import io.ktor.server.application.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger


val repositoryModule = module {
    singleOf(::UserRepositoryImpl).bind<UserRepository>()
    singleOf(::TokenRepositoryImpl).bind<TokenRepository>()
}

val serviceModule = module {
    singleOf(::JwtService)
    singleOf(::AuthService)
    singleOf(::TransactionService)
}

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(
            repositoryModule, serviceModule
        )
    }
}
