package com.nevrozq.pansion.utils

import com.nevrozq.pansion.database.tokens.Tokens
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import com.nevrozq.pansion.database.users.Users
import io.ktor.server.application.ApplicationCall
import server.Moderation
import server.Roles
import server.twoNums
import java.lang.IllegalArgumentException
import java.time.LocalDateTime
import java.util.*

fun List<String>?.toStr(): String? = this?.joinToString(";-")
fun String?.toList(): List<String>? = this?.split(";-")

//123456789012345
//00:00-18-sat-13
fun getSixteenTime(): String {
    val time = LocalDateTime.now()
    return "${time.hour.twoNums()}:" +
            "${time.minute.twoNums()}-" +
            "${time.dayOfMonth.twoNums()}-" +
            "${time.month.toString().subSequence(0, 3)}-" +
            "${time.year.toString().subSequence(2, 4)}"
}



fun createLogin(name: String, surname: String): String {
    val nameSubstring = if (name.lowercase().latin().length < 2) name.lowercase().latin() else name.lowercase().latin().subSequence(0, 1)
    return ("${nameSubstring}.${surname.lowercase().latin()}".cut(27).plus("${Users.getCount() + 1}")).replace(" ", "").replace("-", "").cut(30)
}

fun String.cut(size: Int): String {
    return if (this.length > size) {
        this.subSequence(0, size).toString()
    } else {
        this
    }
}

fun String.latin() = this.replace("а", "a")
    .replace("б", "b")
    .replace("в", "v")
    .replace("г", "g")
    .replace("д", "d")
    .replace("е", "e")
    .replace("ё", "yo")
    .replace("ж", "j")
    .replace("з", "z")
    .replace("и", "i")
    .replace("к", "k")
    .replace("л", "l")
    .replace("м", "m")
    .replace("н", "n")
    .replace("о", "o")
    .replace("п", "p")
    .replace("р", "r")
    .replace("с", "s")
    .replace("т", "t")
    .replace("у", "u")
    .replace("ф", "f")
    .replace("х", "h")
    .replace("ц", "c")
    .replace("ч", "ch")
    .replace("ш", "sh")
    .replace("щ", "t ch")
    .replace("ъ", "x")
    .replace("ы", "i")
    .replace("ь", "x")
    .replace("э", "e")
    .replace("ю", "yu")
    .replace("я", "ya")
    .replace("й", "y")

fun String?.toId(): UUID = try {
    UUID.fromString(this)
} catch (e: IllegalArgumentException) {
    UUID.fromString("00000000-0000-0000-0000-000000000000")
}

object UUIDSerializer : KSerializer<UUID> {
    override val descriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID {
        return decoder.decodeString().toId()
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }
}

//fun String.isTeacher() = Users.getRole(this) == Roles.teacher

//fun String.isModer(): Boolean {
//    val moderation = Users.getModeration(this)
//    return moderation != Moderation.mentor && moderation != Moderation.nothing
//}

val ApplicationCall.login: String get() {
    return Tokens.getLoginOfThisToken(this.request.headers["Bearer-Authorization"].toId())
}

val ApplicationCall.isMember: Boolean get() {
    return Tokens.getIsMember(this.token.toId())
}
val ApplicationCall.isModer: Boolean get() {
    val moderation = Users.getModeration(this.login)
    return moderation != Moderation.mentor && moderation != Moderation.nothing
}
val ApplicationCall.isTeacher: Boolean get() {
    return Users.getRole(this.login) == Roles.teacher
}

val ApplicationCall.token: String?
    get() {
    return this.request.headers["Bearer-Authorization"]
}
