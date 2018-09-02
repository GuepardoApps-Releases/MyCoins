package guepardoapps.mycoins.utils

import android.content.Context
import android.support.annotation.NonNull
import android.util.Log
import guepardoapps.mycoins.database.logging.DbLogging
import guepardoapps.mycoins.database.logging.DbLog
import guepardoapps.mycoins.database.logging.Severity
import java.sql.Date
import java.util.*

internal class Logger private constructor() {
    var loggingEnabled: Boolean = true
    private var writeToDatabaseEnabled: Boolean = true
    private var dbHandler: DbLogging? = null

    private object Holder {
        val instance: Logger = Logger()
    }

    companion object {
        val instance: Logger by lazy { Holder.instance }
    }

    fun initialize(context: Context) {
        if (dbHandler != null) {
            return
        }
        dbHandler = DbLogging(context)
    }

    fun <T> verbose(@NonNull tag: String, @NonNull description: T) {
        if (loggingEnabled) {
            Log.v(tag, description.toString())
            tryToWriteToDatabase(tag, description, Severity.Verbose)
        }
    }

    fun <T> debug(@NonNull tag: String, @NonNull description: T) {
        if (loggingEnabled) {
            Log.d(tag, description.toString())
            tryToWriteToDatabase(tag, description, Severity.Debug)
        }
    }

    fun <T> info(@NonNull tag: String, @NonNull description: T) {
        if (loggingEnabled) {
            Log.i(tag, description.toString())
            tryToWriteToDatabase(tag, description, Severity.Info)
        }
    }

    fun <T> warning(@NonNull tag: String, @NonNull description: T) {
        if (loggingEnabled) {
            Log.w(tag, description.toString())
            tryToWriteToDatabase(tag, description, Severity.Warning)
        }
    }

    fun <T> error(@NonNull tag: String, @NonNull description: T) {
        if (loggingEnabled) {
            Log.e(tag, description.toString())
            tryToWriteToDatabase(tag, description, Severity.Error)
        }
    }

    private fun <T> tryToWriteToDatabase(@NonNull tag: String, @NonNull description: T, severity: Severity) {
        if (dbHandler != null && writeToDatabaseEnabled) {
            dbHandler?.addLog(
                    DbLog(-1,
                            Date(Calendar.getInstance().timeInMillis),
                            severity,
                            tag,
                            description.toString()))
        }
    }
}