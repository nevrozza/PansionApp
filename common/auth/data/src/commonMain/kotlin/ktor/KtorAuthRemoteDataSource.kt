package ktor

import auth.ActivationReceive
import auth.ActivationResponse
import auth.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.path

class KtorAuthRemoteDataSource(
    private val httpClient: HttpClient
) {
    suspend fun performLogin(request: LoginReceive) : LoginResponse {
        return httpClient.post {
            url {
                path("server/auth/login")
                setBody(request)
            }
        }.body()
    }

    suspend fun checkUserActivation(request: CheckActivationReceive) : CheckActivationResponse {
        return httpClient.post {
            url {
                path("server/auth/check")
                setBody(request)
            }
        }.body()
    }

    suspend fun activate(request: ActivationReceive) : ActivationResponse {
        return httpClient.post {
            url {
                path("server/auth/activate")
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