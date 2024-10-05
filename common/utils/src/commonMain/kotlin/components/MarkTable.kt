package components

data class MarkTableItem(
    val content: String,
    val login: String,
    val reason: String,
    val reportId: Int,
    val module: String,
    val date: String? = null,
    val deployDate: String? = null,
    val deployTime: String? = null,
    val deployLogin: String? = null,
    val isTransparent: Boolean = false,
    val groupId: Int? = null,
    val onClick: (reportId: Int) -> Unit
)