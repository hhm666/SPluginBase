package net.mcpes.summit.hhm.base.listener

import cn.nukkit.Server
import cn.nukkit.event.EventHandler
import cn.nukkit.event.EventPriority
import cn.nukkit.event.Listener
import cn.nukkit.event.player.PlayerJoinEvent
import net.mcpes.summit.hhm.base.utils.NukkitUtils

class FloatingTextListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        Server.getInstance().scheduler.scheduleDelayedTask({
            NukkitUtils.floatingTextMap.values.filter { it.pos.level == player.level }.forEach { player.dataPacket(it.addEntityPacket) }
        }, 60)
    }
}