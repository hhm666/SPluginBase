package net.mcpes.summit.hhm.base.command

import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import net.mcpes.summit.hhm.base.utils.language.LanguageUtils
import net.mcpes.summit.hhm.base.utils.language.translate

class LanguageSelectCommand : Command("lang") {
    override fun execute(sender: CommandSender, s: String, strings: Array<out String>): Boolean {
        if (strings.isEmpty()) {
            sender.sendMessage("spluginbase.command.lang.notFound" translate arrayOf())
            return false
        }
        when (strings[0]) {
            "set" -> {
                if (strings.size != 2) {
                    sender.sendMessage("spluginbase.command.lang.notFound" translate arrayOf())
                    return false
                }
                LanguageUtils.setLang(strings[1], sender)
            }
            "list" -> {
                LanguageUtils.sendLanguageList(sender)
            }
            "help" -> {
                sender.sendMessage("spluginbase.command.lang.help" translate arrayOf())
            }
            else -> {
                sender.sendMessage("spluginbase.command.lang.notFound" translate arrayOf())
                return false
            }
        }
        return true
    }
}