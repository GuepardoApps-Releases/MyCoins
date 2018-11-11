package guepardoapps.mycoins.services.api

import guepardoapps.mycoins.models.CoinType
import guepardoapps.mycoins.enums.DownloadType

internal interface OnApiServiceListener {
    fun onFinished(downloadType: DownloadType, coinType: CoinType, jsonString: String, success: Boolean)
}