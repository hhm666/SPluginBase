package net.mcpes.summit.hhm.base.config

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.mcpes.summit.hhm.base.utils.Charset
import org.apache.commons.io.FileUtils
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.LinkedHashMap

/**
 * FoundHiPro
 *
 * @author hhm Copyright (c) 2018/8/29
 * version 1.0
 */
class Config(var file: File, private var data: ConfigData) : ConfigOperation {
    var type: ConfigType = ConfigType.DETECT
    var content: LinkedHashMap<String, Any>
        get() = data.getAll()
        set(value) {
            data.clear()
            value.forEach { t, u -> data[t] = u }
        }

    private var correct = false

    constructor(file: String) : this(File(file))

    constructor(file: File) : this(file, ConfigType.DETECT)

    constructor(file: String, type: ConfigType) : this(File(file), type)

    constructor(file: File, type: ConfigType) : this(file, ConfigData()) {
        this.type = type
    }

    constructor(file: File, type: ConfigType, data: LinkedHashMap<*, *>) : this(file, ConfigData(data)) {
        this.type = type
    }

    init {
        this.load()
    }

    fun reload() {
        this.data.clear()
        this.correct = false
        this.load()
    }

    fun load(): Boolean {
        return this.load(ConfigData())
    }

    fun load(defaultMap: ConfigData): Boolean {
        this.correct = true
        if (!this.file.exists()) {
            try {
                this.file.createNewFile()
            } catch (e: IOException) {
                //MainLogger.getLogger().error("Could not create Config " + this.file.toString())
                //TODO LOG
            }
            this.data = defaultMap
            this.save()
        } else {
            if (this.type == ConfigType.DETECT) {
                var extension = ""
                if (this.file.name.lastIndexOf(".") != -1 && this.file.name.lastIndexOf(".") != 0) {
                    extension = this.file.name.substring(this.file.name.lastIndexOf(".") + 1)
                }
                if (format.containsKey(extension)) {
                    this.type = format[extension]!!
                } else {
                    this.correct = false
                }
            }
            if (this.correct) {
                var content = ""
                try {
                    content = FileUtils.readFileToString(this.file, Charset.defaultCharset)
                } catch (e: IOException) {
                    //APIFunction.getInstance().getLogger().logException(e)
                    //TODO LOG
                }
                this.parseContent(content)
                if (!this.correct) return false
                if (this.setDefault(defaultMap) > 0) {
                    this.save()
                }
            } else {
                return false
            }
        }
        return true
    }

    fun setDefault(map: LinkedHashMap<String, Any>): Int {
        return setDefault(ConfigData(map))
    }

    fun setDefault(map: ConfigData): Int {
        val size = this.data.size
        this.data = this.fillDefaults(map, this.data)
        return this.data.size - size
    }

    override fun getAll(): LinkedHashMap<String, Any> {
        return this.data.getAll()
    }

    override operator fun get(key: String): Any? {
        return this.data[key]
    }

    override operator fun <T> get(key: String, defaultValue: T?): T? {
        return this.data[key, defaultValue]
    }

    override fun getKeys(child: Boolean): Set<String> {
        return this.data.getKeys(child)
    }

    override fun set(key: String, value: Any) {
        this.data[key] = value
    }

    override fun remove(key: String) {
        this.data.remove(key)
    }

    private fun parseContent(content: String) {
        when (this.type) {
            ConfigType.PROPERTIES -> this.parseProperties(content)
            ConfigType.JSON -> {
                val builder = GsonBuilder()
                val gson = builder.create()
                this.data = ConfigData(gson.fromJson(content, object : TypeToken<LinkedHashMap<String, Any>>() {}.type))
            }
            ConfigType.YAML -> {
                val dumperOptions = DumperOptions()
                dumperOptions.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
                val yaml = Yaml(dumperOptions)
                val dt = yaml.loadAs(content, LinkedHashMap::class.java)
                this.data = if (dt == null) {
                    ConfigData()
                } else {
                    ConfigData(dt)
                }
            }
            // case ConfigType.SERIALIZED
            ConfigType.ENUM -> this.parseList(content)
            else -> this.correct = false
        }
    }

    private fun fillDefaults(defaultMap: ConfigData, data: ConfigData): ConfigData {
        for (key in defaultMap.keys) {
            if (!data.containsKey(key)) {
                data[key] = defaultMap[key]!!
            }
        }
        return data
    }

    private fun parseList(content: String) {
        var s = content
        s = s.replace("\r\n", "\n")
        for (v in s.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            if (v.trim { it <= ' ' }.isEmpty()) {
                continue
            }
            data[v] = true
        }
    }

    private fun writeProperties(): String {
        var content = "#Properties Config file\r\n#" + SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Date()) + "\r\n"
        for (entry in this.data.entries) {
            var v = entry.value
            val k = entry.key
            if (v is Boolean) {
                v = if (v) "on" else "off"
            }
            content += "$k=$v\r\n"
        }
        return content
    }

    private fun parseProperties(content: String) {
        for (line in content.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            if (Pattern.compile("[a-zA-Z0-9\\-_.]*+=+[^\\r\\n]*").matcher(line).matches()) {
                val b = line.split("=".toRegex()).toTypedArray()
                val k = b[0]
                val v = b[1].trim { it <= ' ' }
                val vLower = v.toLowerCase()
                if (this.data.containsKey(k)) {
                    //MainLogger.getLogger().debug("[Config] Repeated property " + k + " on file " + this.file.toString())
                    //todo log
                }
                when (vLower) {
                    "on", "true", "yes" -> this.data[k] = true
                    "off", "false", "no" -> this.data[k] = false
                    else -> this.data[k] = v
                }
            }
        }
    }

    fun check(): Boolean {
        return this.correct
    }

    fun isCorrect(): Boolean {
        return correct
    }

    /**
     * Save configuration into provided file. Internal file object will be set to new file.
     *
     * @param file
     * @return
     */

    fun save(file: File): Boolean {
        this.file = file
        return save()
    }

    fun clear() {
        this.data.clear()
    }

    fun save(): Boolean {
        var content = ""
        when (this.type) {
            ConfigType.PROPERTIES -> content = this.writeProperties()
            ConfigType.JSON -> content = GsonBuilder().setPrettyPrinting().create().toJson(this.getAll())
            ConfigType.YAML -> {
                val dumperOptions = DumperOptions()
                dumperOptions.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
                val yaml = Yaml(dumperOptions)
                content = yaml.dump(this.getAll())
            }
            ConfigType.ENUM -> this.data.entries.forEach {
                content += it.key + "\r\n"
            }
            else -> {

            }
        }
        try {
            FileUtils.writeStringToFile(file, content, Charset.defaultCharset)
        } catch (e: IOException) {
            //TODO LOG
        }
        return true
    }

    companion object {
        val format: MutableMap<String, ConfigType> = TreeMap()

        init {
            format["properties"] = ConfigType.PROPERTIES
            format["con"] = ConfigType.PROPERTIES
            format["conf"] = ConfigType.PROPERTIES
            format["config"] = ConfigType.PROPERTIES
            format["js"] = ConfigType.JSON
            format["json"] = ConfigType.JSON
            format["yml"] = ConfigType.YAML
            format["yaml"] = ConfigType.YAML
            format["txt"] = ConfigType.ENUM
            format["list"] = ConfigType.ENUM
            format["enum"] = ConfigType.ENUM
        }
    }

    enum class ConfigType(val type: Int) {
        DETECT(-1), PROPERTIES(0), CNF(0), ENUM(5), ENUMERATION(5), YAML(2), JSON(3);
    }
}