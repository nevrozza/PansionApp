package ktor

import RequestPaths
import auth.RChangeLogin
import auth.RFetchAllDevicesResponse
import auth.RTerminateDeviceReceive
import checkOnNoOk
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.path
import registration.ScanRequestQRReceive
import registration.ScanRequestQRResponse
import registration.SendRegistrationRequestReceive

class KtorSettingsRemoteDataSource(
    private val httpClient: HttpClient
) {
    suspend fun fetchDevices(): RFetchAllDevicesResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Auth.FetchAllDevices)
            }
        }.body()
    }
    suspend fun changeLogin(r: RChangeLogin) {
        httpClient.post {
            url {
                bearer()
                path(RequestPaths.Auth.ChangeLogin)
                setBody(r)
            }
        }.status.value.checkOnNoOk()
    }

    suspend fun terminateDevice(r: RTerminateDeviceReceive) {
        httpClient.post {
            url {
                bearer()
                path(RequestPaths.Auth.TerminateDevice)
                setBody(r)
            }
        }.status.value.checkOnNoOk()
    }
    suspend fun scanRegistrationQR(r: ScanRequestQRReceive) : ScanRequestQRResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Registration.ScanQR)
                setBody(r)
            }
        }.body()
    }
    suspend fun sendRegistrationRequest(r: SendRegistrationRequestReceive) {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Registration.SendRequest)
                setBody(r)
            }
        }.status.value.checkOnNoOk()
    }

//    suspend fun logout(token: String) {
//        httpClient.post {
//            url {
//
//                header("Bearer-Authorization", token)
//                path(RequestPaths.Tokens.logout)
//            }
//        }
//    }
//    suspend fun performLogin(request: LoginReceive) : LoginResponse {
//        return httpClient.post {
//            url {
//                path(RequestPaths.Auth.PerformLogin)
//                setBody(request)
//            }
//        }.body()
//    }
//
//    suspend fun checkUserActivation(request: CheckActivationReceive) : CheckActivationResponse {
//        return httpClient.post {
//            url {
//                path(RequestPaths.Auth.CheckActivation)
//                setBody(request)
//            }
//        }.body()
//    }
//
//    suspend fun activate(request: ActivationReceive) : ActivationResponse {
//        return httpClient.post {
//            url {
//                path(RequestPaths.Auth.ActivateProfile)
//                setBody(request)
//            }
//        }.body()
//    }

//    suspend fun performCheckActivation(request: CheckActivationReceive) : CheckActivationResponse {
//        return httpClient.post {
//            url {
//                path("check/auth")
//                setBody(request)
//            }
//        }.body()
//    }
}