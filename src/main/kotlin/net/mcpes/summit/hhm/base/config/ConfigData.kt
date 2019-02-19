package net.mcpes.summit.hhm.base.config

import java.util.*

/**
 * FoundHiPro
 *
 * @author hhm Copyright (c) 2018/8/29
 * version 1.0
 */
class ConfigData : LinkedHashMap<String, Any>, ConfigOperation {
    constructor() : super()

    constructor(data: LinkedHashMap<*, *>) : this() {
        if (data.isEmpty()) return
        for (entry in data.entries) {
            if (entry.value is LinkedHashMap<*, *>) {
                this[entry.key.toString()] = ConfigData(entry.value as LinkedHashMap<*, *>)
            } else {
                this[entry.key.toString()] = entry.value
            }
        }
    }

    override fun getAll() = ConfigData(this)

    /**
     * Get object by key. If section does not contain value, return null
     */
    override operator fun get(key: String): Any? {
        return this.get<Any>(key, null)
    }

    /**
     * Get object by key. If section does not contain value, return default value
     *
     * @param key
     * @param defaultValue
     * @return
     */
    @Suppress("UNCHECKED_CAST")
    override operator fun <T> get(key: String, defaultValue: T?): T? {
        if (key.isEmpty()) return defaultValue
        if (super.containsKey(key)) return try {
            super.get(key) as T?
        } catch (e: Throwable) {
            defaultValue
        }
        val keys = key.split("\\.".toRegex(), 2).toTypedArray()
        if (!super.containsKey(keys[0])) return defaultValue
        val value = super.get(keys[0])
        if (value != null && value is ConfigData) {
            return value[keys[1], defaultValue]
        }
        return defaultValue
    }

    /**
     * Store value into config section
     *
     * @param key
     * @param value
     */
    override operator fun set(key: String, value: Any) {
        val subKeys = key.split("\\.".toRegex(), 2).toTypedArray()
        if (subKeys.size > 1) {
            var childSection = ConfigData()
            if (this.containsKey(subKeys[0]) && super.get(subKeys[0]) is ConfigData)
                childSection = super.get(subKeys[0]) as ConfigData
            childSection[subKeys[1]] = value
            super.put(subKeys[0], childSection)
        } else
            super.put(subKeys[0], value)
    }

    override fun remove(key: String) {
        if (key.isEmpty()) return
        if (super.containsKey(key))
            super.remove(key)
        else if (this.containsKey(".")) {
            val keys = key.split("\\.".toRegex(), 2).toTypedArray()
            if (super.get(keys[0]) is ConfigData) {
                val section = super.get(keys[0]) as ConfigData
                section.remove(keys[1])
            }
        }
    }

    override fun getKeys(child: Boolean): Set<String> {
        val keys = LinkedHashSet<String>()
        this.entries.forEach { entry ->
            keys.add(entry.key)
            if (entry.value is ConfigData) {
                if (child)
                    (entry.value as ConfigData).getKeys(true).forEach { childKey -> keys.add(entry.key + "." + childKey) }
            }
        }
        return keys
    }
}