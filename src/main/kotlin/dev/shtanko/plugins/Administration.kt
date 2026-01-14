package dev.shtanko.plugins

//import io.github.flaxoos.ktor.server.plugins.taskscheduling.TaskScheduling
//import io.github.flaxoos.ktor.server.plugins.taskscheduling.managers.lock.database.DefaultTaskLockTable
//import io.github.flaxoos.ktor.server.plugins.taskscheduling.managers.lock.database.jdbc
//import org.jetbrains.exposed.sql.SchemaUtils
//import org.jetbrains.exposed.sql.transactions.transaction
import io.github.flaxoos.ktor.server.plugins.ratelimiter.RateLimiting
import io.github.flaxoos.ktor.server.plugins.ratelimiter.implementations.TokenBucket
import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlin.time.Duration.Companion.seconds

fun Application.configureAdministration() {

//    install(TaskScheduling) {
//        jdbc("my jdbc manager") { // <-- given a name, a manager can be explicitly selected for a task
//            database = org.jetbrains.exposed.sql.Database.connect(
//                url = "jdbc:postgresql://host:port",
//                driver = "org.postgresql.Driver",
//                user = "my_username",
//                password = "my_password"
//            ).also {
//                transaction { SchemaUtils.create(DefaultTaskLockTable) }
//            }
//        }
//        task { // if no taskManagerName is provided, the task would be assigned to the default manager
//            name = "My task"
//            task = { taskExecutionTime ->
//                log.info("My task is running: $taskExecutionTime")
//            }
//            kronSchedule = {
//                hours {
//                    from(0).every(12)
//                }
//                minutes {
//                    from(10).every(30)
//                }
//            }
//            concurrency = 2
//        }
//
//        task(taskManagerName = "my jdbc manager") {
//            name = "My Jdbc task"
//            // rest of task config
//        }
//    }

    routing {
        route("/") {
            install(RateLimiting) {
                rateLimiter {
                    type = TokenBucket::class
                    capacity = 100
                    rate = 10.seconds
                }
            }
        }
    }
}
