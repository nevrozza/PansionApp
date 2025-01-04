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
    private val hc: HttpClient
) {
    suspend fun fetchDevices(): RFetchAllDevicesResponse =
        hc.dPost(RequestPaths.Auth.FetchAllDevices).dBody()

    suspend fun changeLogin(r: RChangeLogin): Boolean =
        hc.dPost(RequestPaths.Auth.ChangeLogin, r).check()


    suspend fun terminateDevice(r: RTerminateDeviceReceive): Boolean =
        hc.dPost(RequestPaths.Auth.TerminateDevice, r).check()


    suspend fun scanRegistrationQR(r: ScanRequestQRReceive): ScanRequestQRResponse =
        hc.dPost(RequestPaths.Registration.ScanQR, r).dBody()


    suspend fun sendRegistrationRequest(r: SendRegistrationRequestReceive): Boolean =
        hc.dPost(RequestPaths.Registration.SendRequest, r).check()
}