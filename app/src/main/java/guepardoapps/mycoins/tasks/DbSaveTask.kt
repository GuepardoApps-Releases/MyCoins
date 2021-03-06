package guepardoapps.mycoins.tasks

import android.os.AsyncTask

internal class DbSaveTask : AsyncTask<String, Void, String>() {

    lateinit var method: () -> Unit

    override fun doInBackground(vararg requestUrls: String?): String {
        method.invoke()
        return "OK"
    }

    override fun onPostExecute(result: String?) {}
}