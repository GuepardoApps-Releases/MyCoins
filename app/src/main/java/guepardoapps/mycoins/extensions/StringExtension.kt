package guepardoapps.mycoins.extensions

import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan

internal fun createErrorText(errorString: String): SpannableStringBuilder {
    val spannableStringBuilder = SpannableStringBuilder(errorString)
    spannableStringBuilder.setSpan(ForegroundColorSpan(Color.RED), 0, errorString.length, 0)
    return spannableStringBuilder
}