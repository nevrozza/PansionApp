package ktor

import RequestPaths
import auth.ActivationReceive
import auth.ActivationResponse
import auth.*
import checkOnNoOk
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.path
import registration.FetchLoginsReceive
import registration.FetchLoginsResponse
import server.delayForNewQRToken

class KtorAuthRemoteDataSource(
    private val httpClient: HttpClient
) {

    suspend fun fetchQRToken(r: RFetchQrTokenReceive) : RFetchQrTokenResponse {
        return httpClient.post {
            url {
                setBody(r)
                path(RequestPaths.Auth.FetchQRToken)
            }
        }.body()
    }
    suspend fun fetchLogins(r: FetchLoginsReceive) : FetchLoginsResponse {
        return httpClient.post {
            url {
                setBody(r)
                path(RequestPaths.Registration.FetchLogins)
            }
        }.body()
    }
    suspend fun activateQRTokenAtAll(r: RFetchQrTokenResponse) {
        httpClient.post {
            url {
                bearer()
                setBody(r)
                path(RequestPaths.Auth.ActivateQRTokenAtAll)
            }
        }.status.value.checkOnNoOk()
    }
    suspend fun activateQRToken(r: RFetchQrTokenResponse) : RActivateQrTokenResponse {
        return httpClient.post {
            url {
                bearer()
                setBody(r)
                path(RequestPaths.Auth.ActivateQRToken)
            }
        }.body()
    }
    suspend fun pollQRToken(r: RFetchQrTokenReceive) : LoginResponse {
        return httpClient.post {
            this.timeout {
                this.requestTimeoutMillis = delayForNewQRToken
            }
            url {
                setBody(r)
                path(RequestPaths.Auth.PollQRToken)
            }
        }.body()
    }

    suspend fun fetchAboutMe(r: RFetchAboutMeReceive): RFetchAboutMeResponse {
        return httpClient.post {
            url {
                bearer()
                setBody(r)
                path(RequestPaths.Auth.FetchAboutMe)
            }
        }.body()
    }
    suspend fun checkPickedGIA(r: RCheckGIASubjectReceive){
        httpClient.post {
            url {
                bearer()
                setBody(r)
                path(RequestPaths.Auth.CheckGIASubject)
            }
        }.status.value.checkOnNoOk()
    }

    suspend fun checkConnection(): RCheckConnectionResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Auth.CheckConnection)
            }
        }.body()
    }


    suspend fun logout(token: String) {
        httpClient.post {
            url {

                header("Bearer-Authorization", token)
                path(RequestPaths.Auth.Logout)
            }
        }.status.value.checkOnNoOk()
    }

    suspend fun changeAvatarId(r: RChangeAvatarIdReceive) {
        httpClient.post {
            url {
                bearer()
                path(RequestPaths.Auth.ChangeAvatarId)
                setBody(r)
            }
        }.status.value.checkOnNoOk()
    }

    suspend fun performLogin(request: LoginReceive): LoginResponse {
        return httpClient.post {
            url {
                path(RequestPaths.Auth.PerformLogin)
                setBody(request)
            }
        }.body()
    }

    suspend fun checkUserActivation(request: CheckActivationReceive): CheckActivationResponse {
        return httpClient.post {
            url {
                path(RequestPaths.Auth.CheckActivation)
                setBody(request)
            }
        }.body()
    }

    suspend fun activate(request: ActivationReceive): ActivationResponse {
        return httpClient.post {
            url {
                path(RequestPaths.Auth.ActivateProfile)
                setBody(request)
            }
        }.body()
    }

//    suspend fun performCheckActivation(request: CheckActivationReceive) : CheckActivationResponse {
//        return httpClient.post {
//            url {
//                path("check/auth")
//                setBody(request)
//            }
//        }.body()
//    }
}