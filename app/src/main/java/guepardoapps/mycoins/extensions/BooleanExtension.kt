package guepardoapps.mycoins.extensions

internal fun Boolean.toInteger(): Int {
    if (this) {
        return 1
    }
    return 0
}