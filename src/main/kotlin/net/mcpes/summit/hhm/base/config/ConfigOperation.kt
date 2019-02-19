package net.mcpes.summit.hhm.base.config

import java.util.*

/**
 * FoundHiPro
 *
 * @author hhm Copyright (c) 2018/8/29
 * version 1.0
 */
interface ConfigOperation {
    fun getAll(): LinkedHashMap<String, Any>

    operator fun get(key: String): Any?

    operator fun <T> get(key: String, defaultValue: T?): T?

    operator fun set(key: String, value: Any)

    fun isSection(key: String) = this[key] is ConfigData

    fun getSection(key: String) = this[key, ConfigData()] ?: ConfigData()

    fun getSections(): ConfigData {
        return getSections(null)
    }

    /**
     * Get sections (and only sections) from provided path
     *
     * @param key - config section path, if null or empty root path will used.
     * @return
     */
    fun getSections(key: String?): ConfigData {
        val sections = ConfigData()
        val parent = (if (key == null || key.isEmpty()) this.getAll() else getSection(key))
        parent.entries.forEach { e ->
            if (e.value is ConfigData)
                sections[e.key] = e.value
        }
        return sections
    }

    /**
     * Get int value of config section element
     *
     * @param key - key (inside) current section (default value equals to 0)
     * @return
     */
    fun getInt(key: String) = this.getInt(key, 0)

    /**
     * Get int value of config section element
     *
     * @param key          - key (inside) current section
     * @param defaultValue - default value that will returned if section element is not exists
     * @return
     */
    fun getInt(key: String, defaultValue: Int) = this[key, defaultValue]!!.toInt()

    /**
     * Check type of section element defined by key. Return true this element is Integer
     *
     * @param key
     * @return
     */
    fun isInt(key: String) = this[key] is Int

    /**
     * Get long value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getLong(key: String) = this.getLong(key, 0)

    /**
     * Get long value of config section element
     *
     * @param key          - key (inside) current section
     * @param defaultValue - default value that will returned if section element is not exists
     * @return
     */
    fun getLong(key: String, defaultValue: Long) = this[key, defaultValue]!!.toLong()

    /**
     * Check type of section element defined by key. Return true this element is Long
     *
     * @param key
     * @return
     */
    fun isLong(key: String) = this[key] is Long

    /**
     * Get double value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getDouble(key: String) = this.getDouble(key, 0.0)


    /**
     * Get double value of config section element
     *
     * @param key          - key (inside) current section
     * @param defaultValue - default value that will returned if section element is not exists
     * @return
     */
    fun getDouble(key: String, defaultValue: Double) = this[key, defaultValue]!!.toDouble()

    /**
     * Check type of section element defined by key. Return true this element is Double
     *
     * @param key
     * @return
     */
    fun isDouble(key: String) = this[key] is Double

    /**
     * Get String value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getString(key: String) = this.getString(key, "")

    /**
     * Get String value of config section element
     *
     * @param key          - key (inside) current section
     * @param defaultValue - default value that will returned if section element is not exists
     * @return
     */
    fun getString(key: String, defaultValue: String) = this[key, defaultValue].toString()

    /**
     * Check type of section element defined by key. Return true this element is String
     *
     * @param key
     * @return
     */
    fun isString(key: String) = this[key] is String

    /**
     * Get boolean value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getBoolean(key: String) = this.getBoolean(key, false)

    fun getBoolean(key: String, defaultValue: Boolean) = this[key, defaultValue]!!

    fun isBoolean(key: String): Boolean {
        val `val` = get(key)
        return `val` is Boolean
    }

    fun getList(key: String): List<*>? {
        return this.getList(key, null)
    }

    fun getList(key: String, defaultList: List<*>?): List<*>? = this[key, defaultList]

    fun isList(key: String): Boolean {
        val `val` = get(key)
        return `val` is List<*>
    }

    fun getStringList(key: String): List<String> {
        val value = this.getList(key) ?: return ArrayList(0)
        val result = ArrayList<String>()
        for (o in value) {
            if (o is String || o is Number || o is Boolean || o is Char) {
                result.add(o.toString())
            }
        }
        return result
    }

    fun getIntegerList(key: String): List<Int> {
        val list = getList(key) ?: return ArrayList(0)
        val result = ArrayList<Int>()
        for (`object` in list) {
            when (`object`) {
                is Int -> result.add(`object`)
                is String -> try {
                    result.add(Integer.valueOf(`object`))
                } catch (ex: Exception) {
                    //ignore
                }
                is Char -> result.add(`object` as Int)
                is Number -> result.add(`object`.toInt())
            }
        }
        return result
    }

    fun getBooleanList(key: String): List<Boolean> {
        val list = getList(key) ?: return ArrayList(0)
        val result = ArrayList<Boolean>()
        for (`object` in list) {
            if (`object` is Boolean) {
                result.add(`object`)
            } else if (`object` is String) {
                if (java.lang.Boolean.TRUE.toString() == `object`) {
                    result.add(true)
                } else if (java.lang.Boolean.FALSE.toString() == `object`) {
                    result.add(false)
                }
            }
        }
        return result
    }

    fun getDoubleList(key: String): List<Double> {
        val list = getList(key) ?: return ArrayList(0)
        val result = ArrayList<Double>()
        for (`object` in list) {
            when (`object`) {
                is Double -> result.add(`object`)
                is String -> try {
                    result.add(java.lang.Double.valueOf(`object`))
                } catch (ex: Exception) {
                    //ignore
                }
                is Char -> result.add(`object` as Double)
                is Number -> result.add(`object`.toDouble())
            }
        }
        return result
    }

    fun getFloatList(key: String): List<Float> {
        val list = getList(key) ?: return ArrayList(0)
        val result = ArrayList<Float>()
        for (`object` in list) {
            when (`object`) {
                is Float -> result.add(`object`)
                is String -> try {
                    result.add(java.lang.Float.valueOf(`object`))
                } catch (ex: Exception) {
                    //ignore
                }
                is Char -> result.add(`object` as Float)
                is Number -> result.add(`object`.toFloat())
            }
        }
        return result
    }

    fun getLongList(key: String): List<Long> {
        val list = getList(key) ?: return ArrayList(0)
        val result = ArrayList<Long>()
        for (`object` in list) {
            when (`object`) {
                is Long -> result.add(`object`)
                is String -> try {
                    result.add(java.lang.Long.valueOf(`object`))
                } catch (ex: Exception) {
                    //ignore
                }
                is Char -> result.add(`object` as Long)
                is Number -> result.add(`object`.toLong())
            }
        }
        return result
    }

    fun getByteList(key: String): List<Byte> {
        val list = getList(key) ?: return ArrayList(0)
        val result = ArrayList<Byte>()
        for (`object` in list) {
            when (`object`) {
                is Byte -> result.add(`object`)
                is String -> try {
                    result.add(java.lang.Byte.valueOf(`object`))
                } catch (ex: Exception) {
                    //ignore
                }
                is Char -> result.add(`object`.toChar().toByte())
                is Number -> result.add(`object`.toByte())
            }
        }
        return result
    }

    fun getCharacterList(key: String): List<Char> {
        val list = getList(key) ?: return ArrayList(0)
        val result = ArrayList<Char>()
        for (`object` in list) {
            if (`object` is Char) {
                result.add(`object`)
            } else if (`object` is String) {
                if (`object`.length == 1) {
                    result.add(`object`[0])
                }
            } else if (`object` is Number) {
                result.add(`object`.toInt().toChar())
            }
        }
        return result
    }

    fun getShortList(key: String): List<Short> {
        val list = getList(key) ?: return ArrayList(0)
        val result = ArrayList<Short>()
        for (`object` in list) {
            when (`object`) {
                is Short -> result.add(`object`)
                is String -> try {
                    result.add(java.lang.Short.valueOf(`object`))
                } catch (ex: Exception) {
                    //ignore
                }
                is Char -> result.add(`object`.toChar().toShort())
                is Number -> result.add(`object`.toShort())
            }
        }
        return result
    }

    fun getMapList(key: String): List<Map<*, *>> {
        val list = getList(key)
        val result = ArrayList<Map<*, *>>()
        if (list == null) {
            return result
        }
        for (`object` in list) {
            if (`object` is Map<*, *>) {
                result.add(`object`)
            }
        }
        return result
    }

    fun exists(key: String, ignoreCase: Boolean): Boolean {
        var k = key
        if (ignoreCase) k = k.toLowerCase()
        for (existKey in this.getKeys(true)) {
            val k2 = if (ignoreCase) {
                existKey.toLowerCase()
            } else {
                existKey
            }
            if (k2 == k) return true
        }
        return false
    }

    fun remove(key: String)

    fun getKeys(child: Boolean): Set<String>
}