package guepardoapps.mycoins.extensions

fun Char.div(divider: Int): Char {
    return (this.toInt() / divider).toChar()
}