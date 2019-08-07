package guepardoapps.mycoins.database.logging

import java.io.Serializable

internal enum class Severity : Serializable {
    Verbose, Debug, Warning, Error
}