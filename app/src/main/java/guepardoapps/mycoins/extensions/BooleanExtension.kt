package guepardoapps.mycoins.extensions

fun Boolean.toInteger(): Int {
    if (this) {
        return 1
    }
    return 0
}