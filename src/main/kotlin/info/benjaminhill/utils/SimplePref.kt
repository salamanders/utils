package info.benjaminhill.utils

import java.util.prefs.Preferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

// https://stackoverflow.com/questions/66462586/java-preferences-api-with-kotlin-delegated-properties
inline fun <reified T : Any> preference(enclosingClass: Class<*>, key: String, defaultValue: T) =
    PreferenceDelegate(
        preferences = Preferences.userNodeForPackage(enclosingClass),
        key = key,
        defaultValue = defaultValue,
        type = T::class
    )

class PreferenceDelegate<T : Any>(
    private val preferences: Preferences,
    private val key: String,
    private val defaultValue: T,
    private val type: KClass<T>
) : ReadWriteProperty<Any, T> {

    @Suppress("UNCHECKED_CAST")
    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        with(preferences) {
            when (type) {
                Int::class -> putInt(key, value as Int)
                Long::class -> putLong(key, value as Long)
                Float::class -> putFloat(key, value as Float)
                Boolean::class -> putBoolean(key, value as Boolean)
                String::class -> put(key, value as String)
                ByteArray::class -> putByteArray(key, value as ByteArray)
                else -> error("Unsupported preference type $type.")
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return with(preferences) {
            when (type) {
                Int::class -> getInt(key, defaultValue as Int)
                Long::class -> getLong(key, defaultValue as Long)
                Float::class -> getFloat(key, defaultValue as Float)
                Boolean::class -> getBoolean(key, defaultValue as Boolean)
                String::class -> get(key, defaultValue as String)
                ByteArray::class -> getByteArray(key, defaultValue as ByteArray)
                else -> error("Unsupported preference type $type.")
            }
        } as T
    }

}