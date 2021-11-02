package shaprj.openapi.citibankapi.util

import java.util.*

val set = mutableSetOf<UUID>()

fun isTokenValid(token: String) = true

fun createSessionAndReturnUuid() = UUID.randomUUID().apply {
    set.add(this)
}

fun isSessionUuidValid(sessionUuid: UUID?) = set.contains(sessionUuid)