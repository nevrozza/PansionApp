package view

fun String.toRGB(): IntArray {
    val r = this.substring(0, 2).toInt(16) // 16 for hex
    val g = this.substring(2, 4).toInt(16) // 16 for hex
    val b = this.substring(4, 6).toInt(16) // 16 for hex
    return intArrayOf(r, g, b)
}

