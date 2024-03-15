package components.networkInterface

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.listDialog.ListDialogStoreFactory
import components.listDialog.ListItem

sealed interface NetworkState {
    data object None : NetworkState
    data object Loading : NetworkState
    data object Error : NetworkState
}

class NetworkInterface(
    componentContext: ComponentContext,
    storeFactory: StoreFactory
) : ComponentContext by componentContext {

    private val nStore =
        instanceKeeper.getStore {
            NetworkInterfaceStoreFactory(
                storeFactory = storeFactory
            ).create()
        }

    private val _models = MutableValue(NetworkModel())
    val networkModel: Value<NetworkModel> = _models


    fun nSuccess() {
        _models.value = _models.value.copy(state = NetworkState.None, error = "")
    }

    fun nStartLoading() {
        _models.value = _models.value.copy(state = NetworkState.Loading)
    }

    fun nCancelLoading() {
        if (_models.value.error.isNotBlank()) {
            _models.value = _models.value.copy(state = NetworkState.Error)
        } else {
            _models.value = _models.value.copy(state = NetworkState.None)
        }
    }

    fun goToNone() {
        _models.value = _models.value.copy(
            state = NetworkState.None,
            error = "",
            onFixErrorClick = {},
            thanClear = true
        )
    }

    fun nError(
        text: String,
        thanClear: Boolean = true,
        onFixErrorClick: () -> Unit
    ) {
        _models.value = _models.value.copy(
            state = NetworkState.Error,
            error = text,
            onFixErrorClick = onFixErrorClick,
            thanClear = thanClear
        )
    }

    fun fixError() {
        nStore.accept(NetworkInterfaceStore.Intent.OnRetryClick(_models.value.onFixErrorClick))
        if (_models.value.thanClear) _models.value = _models.value.copy(onFixErrorClick = {})
        _models.value = _models.value.copy(thanClear = true)
    }


    data class NetworkModel(
        val state: NetworkState = NetworkState.None,
        val error: String = "",
        val onFixErrorClick: () -> Unit = {},
        val thanClear: Boolean = true
    )
}