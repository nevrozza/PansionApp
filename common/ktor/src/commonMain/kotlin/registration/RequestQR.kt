package registration

import kotlinx.serialization.Serializable

@Serializable
data class OpenRequestQRReceive(
    val formId: Int
)

@Serializable
data class CloseRequestQRReceive(
    val formId: Int
)

@Serializable
data class ScanRequestQRReceive(
    val formId: Int
)
@Serializable
data class ScanRequestQRResponse(
    val formName: String
)

