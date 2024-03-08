package view


enum class WindowScreen {
    Vertical, Horizontal, Expanded
}

enum class WindowSize {
    Compact, Medium, Expanded, TwoPane
}

enum class WindowType {
    Phone, PC
}
//sealed class WindowSize {
//    object Compact : WindowSize()
//    object Medium : WindowSize()
//    object Expanded : WindowSize()
//}
//sealed class WindowScreen {
//    object Vertical : WindowScreen()
//    object Horizontal : WindowScreen()
//    object Expanded : WindowScreen()
//}