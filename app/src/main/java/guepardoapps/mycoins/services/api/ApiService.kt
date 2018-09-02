package guepardoapps.mycoins.services.api

import guepardoapps.mycoins.enums.CoinType
import guepardoapps.mycoins.enums.DownloadResult
import guepardoapps.mycoins.enums.DownloadType
import guepardoapps.mycoins.tasks.ApiRestCallTask
import guepardoapps.mycoins.utils.Logger

internal class ApiService : IApiService {
    private val tag: String = ApiService::class.java.simpleName

    private lateinit var onApiServiceListener: OnApiServiceListener

    override fun setOnApiServiceListener(onApiServiceListener: OnApiServiceListener) {
        this.onApiServiceListener = onApiServiceListener
    }

    override fun load(downloadType: DownloadType, coinType: CoinType, url: String): DownloadResult {
        if (url.isEmpty()) {
            Logger.instance.warning(tag, "load: Url may not be empty")
            return DownloadResult.InvalidUrl
        }

        val task = ApiRestCallTask()
        task.onApiServiceListener = this.onApiServiceListener
        task.downloadType = downloadType
        task.coinType = coinType
        task.execute(url)

        return DownloadResult.Performing
    }
}