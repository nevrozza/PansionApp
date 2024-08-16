import auth.ActivationReceive
import auth.ActivationResponse
import di.Inject
import ktor.KtorAuthRemoteDataSource
import settings.SettingsAuthDataSource
import auth.*

class AuthRepositoryImpl(
    private val remoteDataSource: KtorAuthRemoteDataSource,
    private val cacheDataSource: SettingsAuthDataSource
) : AuthRepository {
    private val cPlatformConfiguration: CommonPlatformConfiguration = Inject.instance()
    override suspend fun checkGIASubject(r: RCheckGIASubjectReceive) {
        remoteDataSource.checkPickedGIA(r)
    }

    override suspend fun fetchAboutMe(studentLogin: String): RFetchAboutMeResponse {
        return remoteDataSource.fetchAboutMe(RFetchAboutMeReceive(studentLogin = studentLogin))
    }

    override suspend fun checkConnection(): RCheckConnectionResponse {
        return remoteDataSource.checkConnection()
    }

    override suspend fun changeAvatarId(avatarId: Int) {
        remoteDataSource.changeAvatarId(RChangeAvatarIdReceive(avatarId = avatarId))
    }

    override fun updateAfterFetch(r: RCheckConnectionResponse) {
        cacheDataSource.updateAfterFetch(r)
    }

    override suspend fun performLogin(login: String, password: String): LoginResponse {
        val r =
            remoteDataSource.performLogin(
                request = LoginReceive(
                    login = login,
                    password = password,
                    deviceName = cPlatformConfiguration.deviceName,
                    deviceType = cPlatformConfiguration.deviceType,
                    deviceId = cPlatformConfiguration.deviceId
                )
            )
//        cacheDataSource.saveToken(response.token)
        cacheDataSource.saveUser(
            token = r.activation.token,
            name = r.activation.user.fio.name,
            surname = r.activation.user.fio.surname,
            praname = r.activation.user.fio.praname,
            role = r.activation.user.role,
            moderation = r.activation.user.moderation,
            login = r.activation.login,
            avatarId = r.avatarId,
            isParent = r.activation.user.isParent,
            birthday = r.activation.user.birthday
        )
        return r
    }

    override suspend fun activate(login: String, password: String): ActivationResponse {
        val r =
            remoteDataSource.activate(
                request = ActivationReceive(
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

    override suspend fun checkActivation(login: String): CheckActivationResponse {
        val r = remoteDataSource.checkUserActivation(
            request = CheckActivationReceive(
                login = login
            )
        )
        return r
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