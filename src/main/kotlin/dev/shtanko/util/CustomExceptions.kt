package dev.shtanko.util

class UnauthorizedException(message: String = "Unauthorized") : RuntimeException(message)

class NotFoundException(message: String = "Not found") : RuntimeException(message)

class ConflictException(message: String = "Conflict") : RuntimeException(message)

class ServerErrorException(message: String = "Internal Server Error Occurred!") : RuntimeException(message)
