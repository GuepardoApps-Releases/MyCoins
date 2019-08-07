package guepardoapps.mycoins.services.api

import guepardoapps.mycoins.models.CoinType
import guepardoapps.mycoins.enums.DownloadResult
import guepardoapps.mycoins.enums.DownloadType
import guepardoapps.mycoins.tasks.ApiRestCallTask
import guepardoapps.mycoins.utils.Logger

internal class ApiService : IApiService {

    private lateinit var onApiServiceListener: OnApiServiceListener

    override fun load(downloadType: DownloadType, coinType: CoinType, url: String): DownloadResult {
        if (url.isEmpty()) {
            Logger.instance.warning(ApiService::class.java.simpleName, "load: Url may not be empty")
            return DownloadResult.InvalidUrl
        }

        val task = ApiRestCallTask().apply {
            this.downloadType = downloadType
            this.coinType = coinType
        }
        task.onApiServiceListener = this.onApiServiceListener
        task.execute(url)

        return DownloadResult.Performing
    }

    override fun setOnApiServiceListener(onApiServiceListener: OnApiServiceListener) {
        this.onApiServiceListener = onApiServiceListener
    }
}