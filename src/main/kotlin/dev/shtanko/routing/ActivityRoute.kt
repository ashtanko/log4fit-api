package dev.shtanko.routing

import dev.shtanko.dto.request.ActivityRequest
import dev.shtanko.service.ActivityService
import dev.shtanko.util.userId
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.activityRoute(activityService: ActivityService) {
    route("/activities") {
        authenticate("access-token-auth") {
            post {
                val userId = call.userId() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val request = call.receive<ActivityRequest>()
                val activity = activityService.createActivity(userId, request)
                if (activity != null) {
                    call.respond(HttpStatusCode.Created, activity)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to log activity")
                }
            }

            get {
                val userId = call.userId() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
                val activities = activityService.getActivitiesByUserId(userId, page, limit)
                call.respond(HttpStatusCode.OK, activities)
            }

            get("/{id}") {
                val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing ID")
                val activity = activityService.getActivityById(id)
                if (activity != null) {
                    call.respond(HttpStatusCode.OK, activity)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Activity not found")
                }
            }
        }
    }
}
