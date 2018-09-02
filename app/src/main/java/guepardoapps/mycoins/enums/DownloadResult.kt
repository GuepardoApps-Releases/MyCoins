package guepardoapps.mycoins.enums

import java.io.Serializable

internal enum class DownloadResult : Serializable {
    Null,
    InvalidUrl,
    Performing
}