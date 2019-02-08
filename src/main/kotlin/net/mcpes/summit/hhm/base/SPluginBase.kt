package net.mcpes.summit.hhm.base

import cn.nukkit.plugin.PluginBase
import net.mcpes.summit.hhm.base.command.LanguageSelectCommand
import net.mcpes.summit.hhm.base.config.Config
import net.mcpes.summit.hhm.base.utils.language.LanguageUtils
import java.io.File

class SPluginBase : PluginBase() {
    override fun onLoad() {
        instance = this
        masterConfigFile = File(this.dataFolder.absolutePath + "/config.yml")
        LanguageUtils.initList()
        if (!masterConfigFile.exists()) {
            LanguageUtils.setLang(LanguageUtils.getDefaultLanguage(), null)
            masterConfig = Config(masterConfigFile, Config.ConfigType.YAML)
            masterConfig["language"] = LanguageUtils.nowLang
            masterConfig.save()
        } else {
            masterConfig = Config(masterConfigFile)
            if (masterConfig.content.isEmpty()) {
                LanguageUtils.setLang(LanguageUtils.getDefaultLanguage(), null)
                masterConfig.reload()
            } else {
                LanguageUtils.nowLang = masterConfig["language"].toString()
                LanguageUtils.load(this, TITLE)
            }
        }
        this.server.commandMap.register("SPluginBase-LanguageSystem", LanguageSelectCommand())
    }

    companion object {
        const val TITLE = "§l§7|§bS§dPlu§agin§cBase§7| §6"
        lateinit var instance: SPluginBase
        lateinit var masterConfigFile: File
        lateinit var masterConfig: Config

        @JvmStatic
        fun info0(msg: String) {
            instance.server.logger.info(msg)
        }

        @JvmStatic
        fun info(msg: String) {
            instance.server.logger.info(TITLE + msg)
        }
    }
}