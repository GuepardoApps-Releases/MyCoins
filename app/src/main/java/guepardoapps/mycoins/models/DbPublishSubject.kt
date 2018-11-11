package guepardoapps.mycoins.models

import guepardoapps.mycoins.enums.DbAction

internal data class DbPublishSubject(val dbAction: DbAction, val percentage: Float)