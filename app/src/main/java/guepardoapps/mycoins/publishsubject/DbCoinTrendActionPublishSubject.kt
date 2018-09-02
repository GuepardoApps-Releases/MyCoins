package guepardoapps.mycoins.publishsubject

import guepardoapps.mycoins.enums.DbAction
import io.reactivex.subjects.PublishSubject

internal class DbCoinTrendActionPublishSubject private constructor() {

    private object Holder {
        val instance: DbCoinTrendActionPublishSubject = DbCoinTrendActionPublishSubject()
    }

    companion object {
        val instance: DbCoinTrendActionPublishSubject by lazy { Holder.instance }
    }

    val publishSubject = PublishSubject.create<DbAction>()!!
}