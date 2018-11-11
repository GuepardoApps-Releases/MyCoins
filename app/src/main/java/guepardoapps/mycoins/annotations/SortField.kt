package guepardoapps.mycoins.annotations

import kotlin.reflect.KClass

@ExperimentalUnsignedTypes
@Target(AnnotationTarget.FUNCTION)
internal annotation class SortField<T : Any> constructor(val field: String, val position: UInt, val type: KClass<T>)