package dev.shtanko.routing

import dev.shtanko.service.ExerciseService
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.exerciseRoutes(exerciseService: ExerciseService) {
    route("/exercises") {
        get {
            val query = call.request.queryParameters["q"]
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10

            val exercises = if (query.isNullOrBlank()) {
                exerciseService.getAllExercises(page, limit)
            } else {
                exerciseService.searchExercises(query, page, limit)
            }
            call.respond(HttpStatusCode.OK, exercises)
        }

        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing ID")
            val exercise = exerciseService.getExerciseById(id)
            if (exercise != null) {
                call.respond(HttpStatusCode.OK, exercise)
            } else {
                call.respond(HttpStatusCode.NotFound, "Exercise not found")
            }
        }
    }
}
