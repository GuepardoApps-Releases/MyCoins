package guepardoapps.mycoins.annotations

@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
internal annotation class JsonKey(val parent: String, val key: String)