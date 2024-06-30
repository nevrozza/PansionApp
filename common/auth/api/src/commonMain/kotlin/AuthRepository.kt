import auth.ActivationResponse
import auth.LoginResponse
import auth.CheckActivationResponse
import auth.RChangeAvatarIdReceive
import auth.RCheckConnectionResponse
import auth.RFetchAboutMeResponse

interface AuthRepository {


    suspend fun fetchAboutMe(studentLogin: String): RFetchAboutMeResponse

    suspend fun checkConnection(): RCheckConnectionResponse

    suspend fun changeAvatarId(avatarId: Int)

    fun updateAfterFetch(r: RCheckConnectionResponse)

    suspend fun performLogin(login: String, password: String): LoginResponse
    suspend fun activate(login: String, password: String): ActivationResponse
    suspend fun checkActivation(login: String): CheckActivationResponse

    fun saveAvatarId(avatarId: Int)

    fun isUserLoggedIn(): Boolean
    fun fetchToken(): String
    fun fetchAvatarId(): Int
    fun deleteToken()
//    fun saveName(name: String)
    fun fetchName(): String
//    fun saveSurname(surname: String)
    fun fetchSurname(): String
//    fun savePraname(praname: String)
    fun fetchPraname(): String
    fun fetchRole(): String
    fun fetchModeration(): String
    fun fetchLogin(): String

    suspend fun logout()
}