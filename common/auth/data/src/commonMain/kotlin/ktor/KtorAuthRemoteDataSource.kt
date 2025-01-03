package ktor

import RFetchGroupDataReceive
import RFetchGroupDataResponse
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import registration.FetchLoginsReceive
import registration.FetchLoginsResponse
import server.delayForNewQRToken
import webload.RFetchUserDataReceive
import webload.RFetchUserDataResponse

class KtorAuthRemoteDataSource(
    private val hc: HttpClient
) {

    suspend fun fetchGroupData(r: RFetchGroupDataReceive): RFetchGroupDataResponse =
        hc.dPost(RequestPaths.WebLoad.FetchGroupData, r).body()

    suspend fun fetchUserData(r: RFetchUserDataReceive): RFetchUserDataResponse =
        hc.dPost(RequestPaths.WebLoad.FetchUserData, r).body()

    suspend fun fetchQRToken(r: RFetchQrTokenReceive): RFetchQrTokenResponse =
        hc.dPost(RequestPaths.Auth.FetchQRToken, r, isBearer = false).body()

    suspend fun fetchLogins(r: FetchLoginsReceive): FetchLoginsResponse =
        hc.dPost(RequestPaths.Registration.FetchLogins, r, isBearer = false).body()

    suspend fun activateQRTokenAtAll(r: RFetchQrTokenResponse) =
        hc.dPost(RequestPaths.Auth.ActivateQRTokenAtAll, r).check()

    suspend fun activateQRToken(r: RFetchQrTokenResponse): RActivateQrTokenResponse =
        hc.dPost(RequestPaths.Auth.ActivateQRToken, r).body()

    suspend fun pollQRToken(r: RFetchQrTokenReceive): LoginResponse =
        hc.dPost(RequestPaths.Auth.PollQRToken, r, isBearer = false) {
            this.timeout {
                this.requestTimeoutMillis = delayForNewQRToken
            }
        }.body()

    suspend fun fetchAboutMe(r: RFetchAboutMeReceive): RFetchAboutMeResponse =
        hc.dPost(RequestPaths.Auth.FetchAboutMe, r).body()

    suspend fun checkPickedGIA(r: RCheckGIASubjectReceive): Boolean =
        hc.dPost(RequestPaths.Auth.CheckGIASubject, r).check()

    suspend fun checkConnection(): RCheckConnectionResponse =
        hc.dPost(RequestPaths.Auth.CheckConnection).body()

    suspend fun logout(token: String): Boolean =
        hc.dPost(RequestPaths.Auth.Logout, isBearer = false) {
            header("Bearer-Authorization", token)
        }.check()


    suspend fun changeAvatarId(r: RChangeAvatarIdReceive) : Boolean =
        hc.dPost(RequestPaths.Auth.ChangeAvatarId, r).check()


    suspend fun performLogin(r: LoginReceive): LoginResponse =
        hc.dPost(RequestPaths.Auth.PerformLogin, r, isBearer = false).body()

    suspend fun checkUserActivation(r: CheckActivationReceive): CheckActivationResponse =
        hc.dPost(RequestPaths.Auth.CheckActivation, r, isBearer = false).body()

    suspend fun activate(r: ActivationReceive): ActivationResponse =
        hc.dPost(RequestPaths.Auth.ActivateProfile, r, isBearer = false).body()
}