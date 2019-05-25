package guepardoapps.mycoins.extensions

import java.util.*

/**
 * @param digits the numbers to show after separator, the decimals
 * @return returns a string with specified format and additional decimal zeros
 */
internal fun Double.doubleFormat(digits: Int): String = String.format(Locale.getDefault(), "%.${digits}f", this)