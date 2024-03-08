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

    override suspend fun performLogin(login: String, password: String): LoginResponse {
        val response =
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
        cacheDataSource.saveUser(response.token, response.name, response.surname, response.praname, response.role, response.moderation)
        return response
    }

    override suspend fun activate(login: String, password: String): ActivationResponse {
        val response =
            remoteDataSource.activate(
                request = ActivationReceive(
                    login = login,
                    password = password,
                    deviceId = cPlatformConfiguration.deviceId,
                    deviceName = cPlatformConfiguration.deviceName,
                    deviceType = cPlatformConfiguration.deviceType
                )
            )
        cacheDataSource.saveUser(response.token, response.name, response.surname, response.praname, response.role, response.moderation)
        return response
    }

    override suspend fun checkActivation(login: String): CheckActivationResponse {
        val response = remoteDataSource.checkUserActivation(
            request = CheckActivationReceive(
                login = login
            )
        )
        return response
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
}