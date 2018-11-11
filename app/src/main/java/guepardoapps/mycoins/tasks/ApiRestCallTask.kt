package guepardoapps.mycoins.tasks

import android.os.AsyncTask
import guepardoapps.mycoins.enums.DownloadType
import guepardoapps.mycoins.models.CoinTypes
import guepardoapps.mycoins.services.api.OnApiServiceListener
import guepardoapps.mycoins.utils.Logger
import okhttp3.OkHttpClient
import okhttp3.Request

internal class ApiRestCallTask : AsyncTask<String, Void, String>() {
    private val tag: String = ApiRestCallTask::class.java.simpleName

    var downloadType = DownloadType.Null
    var coinType = CoinTypes.Null
    lateinit var onApiServiceListener: OnApiServiceListener

    override fun doInBackground(vararg requestUrls: String?): String {
        var result = ""
        if (downloadType == DownloadType.Null) {
            return result
        }

        val okHttpClient = OkHttpClient()
        requestUrls.forEach { requestUrl ->
            try {
                val request = Request.Builder().url(requestUrl!!).build()
                val response = okHttpClient.newCall(request).execute()
                val responseBody = response.body()

                if (responseBody != null) {
                    result = responseBody.string()
                } else {
                    Logger.instance.error(tag, "ResponseBody is null!")
                }
            } catch (exception: Exception) {
                Logger.instance.error(tag, exception)
            }
        }

        return result
    }

    override fun onPostExecute(result: String?) {
        if (result.isNullOrEmpty()) {
            onApiServiceListener.onFinished(downloadType, coinType, "", false)
            return
        }

        onApiServiceListener.onFinished(downloadType, coinType, result, true)
    }
}