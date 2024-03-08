package server

fun Int.twoNums(): String {
    return if (this < 10) {
        "0$this"
    } else this.toString()
}