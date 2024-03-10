package settings

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

class SettingsAuthDataSource(
    private val settings: Settings
) {
    fun saveToken(token: String) {
        settings[tokenKey] = token
    }

    fun fetchToken(): String {
        return settings[tokenKey, ""]
    }

    fun deleteToken() {
        settings[tokenKey] = ""
    }

    fun fetchName(): String {
        return settings[nameKey, ""]
    }

    fun fetchSurname(): String {
        return settings[surnameKey, ""]
    }

    fun saveUser(token: String, name: String, surname: String, praname: String?, role: String, moderation: String) {

        settings[tokenKey] = token
        settings[nameKey] = name
        settings[surnameKey] = surname
        settings[pranameKey] = praname
        settings[roleKey] = role
        settings[moderationKey] = moderation
    }

    fun fetchPraname(): String {
        return settings[pranameKey, ""]
    }

    fun fetchRole(): String {
        return settings[roleKey, "0"]
    }

    fun fetchModeration(): String {
        return settings[moderationKey, "0"]
    }


    companion object {
        const val tokenKey = "tokenPansionAppKey"
        const val nameKey = "nameKey"
        const val surnameKey = "surnameKey"
        const val pranameKey = "pranameKey"
        const val roleKey = "roleKey"
        const val moderationKey = "moderationKey"
    }
}