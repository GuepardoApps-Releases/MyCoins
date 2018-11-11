package guepardoapps.mycoins.services.api

import guepardoapps.mycoins.models.CoinType
import guepardoapps.mycoins.enums.DownloadResult
import guepardoapps.mycoins.enums.DownloadType

internal interface IApiService {
    fun setOnApiServiceListener(onApiServiceListener: OnApiServiceListener)
    fun load(downloadType: DownloadType, coinType: CoinType, url: String): DownloadResult
}