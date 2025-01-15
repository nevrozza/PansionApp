import auth.ActivationReceive
import auth.ActivationResponse
import di.Inject
import ktor.KtorAuthRemoteDataSource
import settings.SettingsAuthDataSource
import auth.*
import registration.FetchLoginsReceive
import registration.FetchLoginsResponse
import webload.RFetchUserDataReceive
import webload.RFetchUserDataResponse

class AuthRepositoryImpl(
    private val remoteDataSource: KtorAuthRemoteDataSource,
    private val cacheDataSource: SettingsAuthDataSource
) : AuthRepository {
    private val cPlatformConfiguration: CommonPlatformConfiguration = Inject.instance()
    override suspend fun changeStatsSettings(r: RChangeStatsSettingsReceive) {
        remoteDataSource.changeStatsSettings(r)
    }


    override suspend fun fetchUserData(r: RFetchUserDataReceive): RFetchUserDataResponse {
        return remoteDataSource.fetchUserData(r)
    }

    override suspend fun fetchGroupData(r: RFetchGroupDataReceive): RFetchGroupDataResponse {
        return remoteDataSource.fetchGroupData(r)
    }

    override suspend fun activateQRTokenAtAll(r: RFetchQrTokenResponse) {
        remoteDataSource.activateQRTokenAtAll(r)
    }

    override suspend fun fetchLogins(): FetchLoginsResponse {
        return remoteDataSource.fetchLogins(
            FetchLoginsReceive(
                deviceId = cPlatformConfiguration.deviceId
            )
        )
    }

    override suspend fun activateQRToken(r: RFetchQrTokenResponse): RActivateQrTokenResponse {
        return remoteDataSource.activateQRToken(r)
    }

    override suspend fun saveUser(
        a: ActivationResponse,
        avatarId: Int
    ) {
        cacheDataSource.saveUser(
            token = a.token,
            name = a.user.fio.name,
            surname = a.user.fio.surname,
            praname = a.user.fio.praname,
            role = a.user.role,
            moderation = a.user.moderation,
            login = a.login,
            avatarId = avatarId,
            isParent = a.user.isParent,
            birthday = a.user.birthday
        )
    }

    override suspend fun fetchQrToken(r: RFetchQrTokenReceive): RFetchQrTokenResponse {
        return remoteDataSource.fetchQRToken(r)
    }

    override suspend fun pollQrToken(r: RFetchQrTokenReceive): LoginResponse {
        return remoteDataSource.pollQRToken(r)
    }

    override suspend fun checkGIASubject(r: RCheckGIASubjectReceive) {
        remoteDataSource.checkPickedGIA(r)
    }

    override suspend fun fetchAboutMe(r: RFetchAboutMeReceive): RFetchAboutMeResponse {
        return remoteDataSource.fetchAboutMe(r)
    }

    override suspend fun checkConnection(): RCheckConnectionResponse {
        return remoteDataSource.checkConnection()
    }

    override suspend fun changeAvatarId(avatarId: Int, price: Int) {
        remoteDataSource.changeAvatarId(RChangeAvatarIdReceive(avatarId = avatarId, price = price))
    }

    override fun updateAfterFetch(r: RCheckConnectionResponse) {
        cacheDataSource.updateAfterFetch(r)
    }

    override suspend fun performLogin(login: String, password: String): LoginResponse {
        val r =
            remoteDataSource.performLogin(
                r = LoginReceive(
                    login = login,
                    password = password,
                    deviceName = cPlatformConfiguration.deviceName,
                    deviceType = cPlatformConfiguration.deviceType,
                    deviceId = cPlatformConfiguration.deviceId
                )
            )
//        cacheDataSource.saveToken(response.token)
        saveUser(r.activation, r.avatarId)
        return r
    }

    override suspend fun activate(login: String, password: String): ActivationResponse {
        val r =
            remoteDataSource.activate(
                r = ActivationReceive(
                    login = login,
                    password = password,
                    deviceId = cPlatformConfiguration.deviceId,
                    deviceName = cPlatformConfiguration.deviceName,
                    deviceType = cPlatformConfiguration.deviceType
                )
            )

        cacheDataSource.saveUser(
            token = r.token,
            name = r.user.fio.name,
            surname = r.user.fio.surname,
            praname = r.user.fio.praname,
            role = r.user.role,
            moderation = r.user.moderation,
            login = r.login,
            avatarId = 0,
            isParent = r.user.isParent,
            birthday = r.user.birthday
        )
        return r
    }

    override suspend fun checkActivation(r: CheckActivationReceive): CheckActivationResponse {
        return remoteDataSource.checkUserActivation(r)
    }

    override fun saveAvatarId(avatarId: Int) {
        cacheDataSource.saveAvatarId(avatarId)
    }

//    override suspend fun register(login: String, password: String, name: String, surname: String, number: String): RegistrationResponse {
//        val token =
//            remoteDataSource.performRegistration(
//                request = RegistrationReceive(
//                    login = login,
//                    password = password,
//                    name = name,
//                    surname = surname,
//                    number = number,
//                    deviceName = cPlatformConfiguration.deviceName,
//                    deviceType = cPlatformConfiguration.deviceType,
//                    deviceId = cPlatformConfiguration.deviceId
//                )
//            )
//        cacheDataSource.saveToken(token.token)
//        return token
//    }
//


//    override suspend fun recoveryVerification(code: String): RecoveryVerificationResponse {
//        val token =
//            remoteDataSource.performRecoveryVerification(
//                request = RecoveryVerificationReceive(
//                    code = code
//                )
//            )
//        cacheDataSource.saveToken(token.token)
//        return token
//    }

//    override suspend fun activation(login: String, newLogin: String, password: String): auth.ActivationResponse {
//        val token =
//            remoteDataSource.performActivation(
//                request = auth.ActivationReceive(
//                    login = login,
//                    newLogin = newLogin,
//                    password = password,
//                    deviceName = cPlatformConfiguration.deviceName,
//                    deviceType = cPlatformConfiguration.deviceType,
//                    deviceId = cPlatformConfiguration.deviceId
//                )
//            )
//
//        cacheDataSource.saveToken(token.token)
//        return token
//    }
//
//    override suspend fun checkActivation(login: String): CheckActivationResponse {
//        return remoteDataSource.performCheckActivation(
//            request = CheckActivationReceive(
//                login = login
//            )
//        )
//    }

    override fun isUserLoggedIn(): Boolean {
        return cacheDataSource.fetchToken().isNotBlank()
    }

    override fun fetchToken(): String {
        return cacheDataSource.fetchToken()
    }

    override fun fetchAvatarId(): Int {
        return cacheDataSource.fetchAvatarId()
    }

    override fun deleteToken() {
        cacheDataSource.deleteToken()
    }

    override fun fetchName(): String {
        return cacheDataSource.fetchName()
    }

    override fun fetchSurname(): String {
        return cacheDataSource.fetchSurname()
    }

    override fun fetchPraname(): String {
        return cacheDataSource.fetchPraname()
    }

    override fun fetchRole(): String {
        return cacheDataSource.fetchRole()
    }

    override fun fetchModeration(): String {
        return cacheDataSource.fetchModeration()
    }

    override fun fetchIsParent(): Boolean {
        return cacheDataSource.fetchIsParent()
    }

    override fun fetchLogin(): String {
        return cacheDataSource.fetchLogin()
    }

    override fun fetchBirthday(): String {
        return cacheDataSource.fetchBirthday()
    }

    override suspend fun logout() {
        val token = cacheDataSource.fetchToken()
        cacheDataSource.logout()
        remoteDataSource.logout(token)
    }
}