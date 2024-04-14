fun Int.checkOnNoOk() {
    if (this != 200) {
        throw Throwable("server error")
    }
}