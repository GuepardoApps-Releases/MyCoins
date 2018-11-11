package guepardoapps.mycoins.extensions

import guepardoapps.mycoins.annotations.SortField
import guepardoapps.mycoins.models.CoinAdapterHolder
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberFunctions

@ExperimentalUnsignedTypes
@Suppress("UNCHECKED_CAST")
internal fun CoinAdapterHolder.getSortFieldMethod(sortFieldName: String): Pair<KCallable<*>?, KClass<*>> {
    val kCallable = this::class.declaredMemberFunctions
            .singleOrNull { (it.annotations.singleOrNull { annotation -> annotation.annotationClass == SortField::class } as? SortField<*>)?.field == sortFieldName }
    val kClass = (this::class.declaredMemberFunctions
            .map { kFunction -> kFunction.annotations.firstOrNull { annotation -> annotation is SortField<*> } }
            .firstOrNull { annotation -> annotation != null && (annotation as SortField<*>).field == sortFieldName }
            as SortField<*>).type
    return Pair(kCallable, kClass)
}
