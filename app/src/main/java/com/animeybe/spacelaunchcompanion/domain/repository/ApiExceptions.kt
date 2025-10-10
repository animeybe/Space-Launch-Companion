package com.animeybe.spacelaunchcompanion.domain.repository

sealed class ApiException(message: String) : Exception(message)

class RateLimitException(message: String, val retryAfterSeconds: Int) : ApiException(message)
class NotFoundException(message: String) : ApiException(message)
class ServerException(message: String) : ApiException(message)