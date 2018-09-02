package guepardoapps.mycoins.extensions

internal fun Char.div(divider: Int): Char {
    return (this.toInt() / divider).toChar()
}