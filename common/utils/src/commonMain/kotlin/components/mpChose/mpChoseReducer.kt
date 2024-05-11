package components.mpChose

import com.arkivanov.mvikotlin.core.store.Reducer

object mpChoseReducer : Reducer<mpChoseStore.State, mpChoseStore.Message> {
    override fun mpChoseStore.State.reduce(msg: mpChoseStore.Message): mpChoseStore.State {
        return when (msg) {
            mpChoseStore.Message.HideDialog -> copy(isDialogShowing = false)

            is mpChoseStore.Message.ShowDialog -> {copy(isDialogShowing = true)}
        }
    }
}