import auth.ActivationResponse
import auth.LoginResponse
import auth.CheckActivationResponse
import auth.RActivateQrTokenResponse
import auth.RCheckConnectionResponse
import auth.RCheckGIASubjectReceive
import auth.RFetchAboutMeResponse
import auth.RFetchQrTokenReceive
import auth.RFetchQrTokenResponse
import registration.FetchLoginsResponse
import webload.RFetchUserDataReceive
import webload.RFetchUserDataResponse

interface AuthRepository {


    suspend fun fetchUserData(r: RFetchUserDataReceive) : RFetchUserDataResponse
    suspend fun fetchGroupData(r: RFetchGroupDataReceive) : RFetchGroupDataResponse
    suspend fun activateQRTokenAtAll(r: RFetchQrTokenResponse)
    suspend fun fetchLogins() : FetchLoginsResponse
    suspend fun activateQRToken(r: RFetchQrTokenResponse) : RActivateQrTokenResponse


    suspend fun saveUser(a: ActivationResponse, avatarId: Int)

    suspend fun fetchQrToken(r: RFetchQrTokenReceive) : RFetchQrTokenResponse
    suspend fun pollQrToken(r: RFetchQrTokenReceive) : LoginResponse

    suspend fun checkGIASubject(r: RCheckGIASubjectReceive)

    suspend fun fetchAboutMe(studentLogin: String): RFetchAboutMeResponse

    suspend fun checkConnection(): RCheckConnectionResponse

    suspend fun changeAvatarId(avatarId: Int, price: Int)

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
    fun fetchIsParent(): Boolean
    fun fetchLogin(): String
    fun fetchBirthday(): String

    suspend fun logout()
}