package settings

import auth.RCheckConnectionResponse
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

class SettingsAuthDataSource(
    private val settings: Settings
) {

    fun saveAvatarId(avatarId: Int) {
        settings[AVATAR_KEY] = avatarId
    }

    fun updateAfterFetch(r: RCheckConnectionResponse) {
        settings[NAME_KEY] = r.name
        settings[SURNAME_KEY] = r.surname
        settings[PRANAME_KEY] = r.praname
        settings[ROLE_KEY] = r.role
        settings[MODERATION_KEY] = r.moderation
        settings[AVATAR_KEY] = r.avatarId
        settings[IS_PARENT_KEY] = r.isParent
        settings[BIRTHDAY_KEY] = r.birthday
    }

    fun logout() {
        settings[TOKEN_KEY] = ""
        settings[LOGIN_KEY] = ""
        settings[NAME_KEY] = ""
        settings[SURNAME_KEY] = ""
        settings[PRANAME_KEY] = ""
        settings[ROLE_KEY] = ""
        settings[MODERATION_KEY] = ""
        settings[AVATAR_KEY] = 0
        settings[IS_PARENT_KEY] = false
        settings[BIRTHDAY_KEY] = ""
    }

//    fun saveToken(token: String) {
//        settings[TOKEN_KEY] = token
//    }

    fun fetchToken(): String {
        return settings[TOKEN_KEY, ""]
    }

    fun fetchLogin(): String {
        return settings[LOGIN_KEY, ""]
    }


    fun deleteToken() {
        settings[TOKEN_KEY] = ""
    }

    fun fetchAvatarId(): Int {
        return settings[AVATAR_KEY, 0]
    }

    fun fetchName(): String {
        return settings[NAME_KEY, ""]
    }

    fun fetchSurname(): String {
        return settings[SURNAME_KEY, ""]
    }

    fun saveUser(token: String, login: String, name: String, surname: String, praname: String?, role: String, moderation: String, avatarId: Int, isParent: Boolean, birthday: String) {
        settings[LOGIN_KEY] = login
        settings[TOKEN_KEY] = token
        settings[NAME_KEY] = name
        settings[SURNAME_KEY] = surname
        settings[PRANAME_KEY] = praname
        settings[ROLE_KEY] = role
        settings[MODERATION_KEY] = moderation
        settings[AVATAR_KEY] = avatarId
        settings[IS_PARENT_KEY] = isParent
        settings[BIRTHDAY_KEY] = birthday
    }

    fun fetchPraname(): String {
        return settings[PRANAME_KEY, ""]
    }

    fun fetchRole(): String {
        return settings[ROLE_KEY, "0"]
    }

    fun fetchModeration(): String {
        return settings[MODERATION_KEY, "0"]
    }
    fun fetchIsParent(): Boolean {
        return settings[IS_PARENT_KEY, false]
    }
    fun fetchBirthday(): String {
        return settings[BIRTHDAY_KEY, ""]
    }


    companion object {
        const val TOKEN_KEY = "tokenPansionAppKey"
        const val LOGIN_KEY = "loginKey"
        const val NAME_KEY = "nameKey"
        const val SURNAME_KEY = "surnameKey"
        const val PRANAME_KEY = "pranameKey"
        const val ROLE_KEY = "roleKey"
        const val MODERATION_KEY = "moderationKey"
        const val AVATAR_KEY = "AVATAR_KEY"
        const val IS_PARENT_KEY = "IS_PARENT_KEY"
        const val BIRTHDAY_KEY = "BIRTHDAY_KEY"
    }
}