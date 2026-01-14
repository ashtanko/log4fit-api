package dev.shtanko.plugins

import dev.shtanko.repository.ActivityRepository
import dev.shtanko.repository.ExerciseRepository
import dev.shtanko.repository.TokenRepository
import dev.shtanko.repository.UserRepository
import dev.shtanko.repository.impl.ActivityRepositoryImpl
import dev.shtanko.repository.impl.ExerciseRepositoryImpl
import dev.shtanko.repository.impl.TokenRepositoryImpl
import dev.shtanko.repository.impl.UserRepositoryImpl
import dev.shtanko.service.ActivityService
import dev.shtanko.service.AuthService
import dev.shtanko.service.ExerciseService
import dev.shtanko.service.GoogleAuthService
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
    singleOf(::ExerciseRepositoryImpl).bind<ExerciseRepository>()
    singleOf(::ActivityRepositoryImpl).bind<ActivityRepository>()
}

val serviceModule = module {
    singleOf(::JwtService)
    singleOf(::AuthService)
    singleOf(::TransactionService)
    singleOf(::GoogleAuthService)
    singleOf(::ExerciseService)
    singleOf(::ActivityService)
}

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(
            repositoryModule, serviceModule
        )
    }
}
