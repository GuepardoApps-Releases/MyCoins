package guepardoapps.mycoins.publishsubject

import guepardoapps.mycoins.enums.DbAction
import io.reactivex.subjects.PublishSubject

internal class DbCoinActionPublishSubject private constructor() {

    private object Holder {
        val instance: DbCoinActionPublishSubject = DbCoinActionPublishSubject()
    }

    companion object {
        val instance: DbCoinActionPublishSubject by lazy { Holder.instance }
    }

    val publishSubject = PublishSubject.create<DbAction>()!!
}