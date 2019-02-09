package net.mcpes.summit.hhm.base.utils.language

import cn.nukkit.command.CommandSender
import cn.nukkit.plugin.PluginBase
import cn.nukkit.utils.Config
import net.mcpes.summit.hhm.base.SPluginBase
import java.io.File
import java.io.InputStream
import java.net.JarURLConnection
import java.net.URL
import java.util.*
import java.util.jar.JarFile

infix fun String.translate(params: Array<Any?>): String {
    return LanguageUtils.translate(this, params)
}

object LanguageUtils {
    val list: HashMap<String, String> = hashMapOf()
    val titles = hashMapOf<PluginBase, String>()
    val configs = hashMapOf<PluginBase, Config>()
    val data: HashMap<String, String> = hashMapOf()
    var nowLang: String = getDefaultLanguage()

    fun load(pluginBase: PluginBase, title: String = "") {
        val config = Config(Config.YAML)
        val f = PluginBase::class.java.getDeclaredField("file")
        f.isAccessible = true
        val file = f.get(pluginBase) as File
        val jar = JarFile(file)
        var entry = jar.getEntry("lang/$nowLang.yml")
        var stream: InputStream?
        if (entry == null) {
            SPluginBase.info("spluginbase.lang.load.useEN_US" translate arrayOf(pluginBase.fullName, list[nowLang], list["en_US"]))
            entry = jar.getEntry("lang/en_US.yml")
            stream = jar.getInputStream(entry)
            if (stream == null) {
                val jarPath = "jar:" + file.toURL().toString() + "!/"
                val jarURL = URL(jarPath)
                val jarCon = jarURL.openConnection() as JarURLConnection
                val jarFile = jarCon.jarFile
                jarFile.entries().iterator().forEach {
                    if (it.name.startsWith("lang/") && !it.isDirectory) {
                        stream = jar.getInputStream(it)
                        val l = it.toString().replace("lang/", "").replace(".yml", "")
                        SPluginBase.info("spluginbase.lang.load.usePluginDefaultLanguage" translate arrayOf(pluginBase.fullName, list["en_US"], list[l]
                                ?: l))
                    }
                }
            }
        } else {
            stream = jar.getInputStream(entry)
        }
        try {
            config.load(stream)
            configs[pluginBase] = config
            config.all.forEach { t, u -> data[t] = u.toString().replace("%TITLE", title) }
            titles[pluginBase] = title
        } catch (e: Throwable) {
            SPluginBase.info("spluginbase.lang.load.pluginNotFound" translate arrayOf(pluginBase.fullName))
            e.printStackTrace()
        }
    }

    fun setLang(key: String, sender: CommandSender? = null) {
        if (sender == null) {
            nowLang = key
            load(SPluginBase.instance, SPluginBase.TITLE)
            SPluginBase.info("spluginbase.lang.defaultSet" translate arrayOf(list[key]))
        } else {
            if (!list.containsKey(key)) {
                sender.sendMessage("spluginbase.lang.set.notFound" translate arrayOf(key))
                return
            }
            nowLang = key
            data.clear()
            load(SPluginBase.instance, SPluginBase.TITLE)
            sender.sendMessage("spluginbase.lang.set.success" translate arrayOf(list[key]))
            SPluginBase.masterConfig["language"] = nowLang
            SPluginBase.masterConfig.save()
        }
        configs.forEach { t, _ ->
            if (t != SPluginBase.instance) this.load(t, titles[t]!!)
        }
    }

    fun getDefaultLanguage() = when (Locale.getDefault()) {
        Locale.SIMPLIFIED_CHINESE -> "zh_CN"
        Locale.TRADITIONAL_CHINESE -> "zh_TW"
        else -> "en_US"
    }

    fun initList() {
        val list = Config(Config.YAML)
        val f = PluginBase::class.java.getDeclaredField("file")
        f.isAccessible = true
        val file = f.get(SPluginBase.instance) as File
        val jar = JarFile(file)
        list.load(jar.getInputStream(jar.getEntry("lang/language.list")))
        list.all.forEach { t, u -> LanguageUtils.list[t] = u.toString() }
    }

    fun sendLanguageList(sender: CommandSender) {
        sender.sendMessage("语言列表/LanguageList")
        list.forEach { t, u -> sender.sendMessage("§6$t:$u") }
    }

    fun translate(key: String, params: Array<out Any?>): String {
        var origin = data[key] ?: "NotFound"
        params.forEachIndexed { index, s ->
            origin = origin.replace("%" + (index + 1), s.toString())
        }
        return origin
    }
}
