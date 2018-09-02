package guepardoapps.mycoins.publishsubject

import guepardoapps.mycoins.enums.DbAction
import io.reactivex.subjects.PublishSubject

internal class DbCoinConversionActionPublishSubject private constructor() {

    private object Holder {
        val instance: DbCoinConversionActionPublishSubject = DbCoinConversionActionPublishSubject()
    }

    companion object {
        val instance: DbCoinConversionActionPublishSubject by lazy { Holder.instance }
    }

    val publishSubject = PublishSubject.create<DbAction>()!!
}