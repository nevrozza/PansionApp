package components.mpChose

import com.arkivanov.mvikotlin.core.store.Reducer

object MpChoseReducer : Reducer<MpChoseStore.State, MpChoseStore.Message> {
    override fun MpChoseStore.State.reduce(msg: MpChoseStore.Message): MpChoseStore.State {
        return when (msg) {
            MpChoseStore.Message.HideDialog -> copy(isDialogShowing = false)

            is MpChoseStore.Message.ShowDialog -> {copy(isDialogShowing = true)}
        }
    }
}