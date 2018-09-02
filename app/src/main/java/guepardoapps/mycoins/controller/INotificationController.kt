package guepardoapps.mycoins.controller

import android.support.annotation.NonNull
import guepardoapps.mycoins.models.NotificationContent

internal interface INotificationController {
    fun create(@NonNull notificationContent: NotificationContent)
    fun close(id: Int)
}