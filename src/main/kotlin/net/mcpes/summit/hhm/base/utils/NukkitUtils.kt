package net.mcpes.summit.hhm.base.utils

import cn.nukkit.Server
import cn.nukkit.entity.Entity
import cn.nukkit.entity.data.EntityMetadata
import cn.nukkit.level.Level
import cn.nukkit.level.Location
import cn.nukkit.level.Position
import cn.nukkit.network.protocol.AddEntityPacket
import cn.nukkit.network.protocol.RemoveEntityPacket
import cn.nukkit.network.protocol.SetEntityDataPacket
import net.mcpes.summit.hhm.base.error.PositionInformationConversionException
import net.mcpes.summit.hhm.base.utils.floating.FloatingTextData
import java.util.*

fun String.toPosition(): Position {
    try {
        val a = this.split(":")
        if (a.size != 4) throw PositionInformationConversionException(RuntimeException("String[]坐标信息数据不足(size!=6)"))
        if (!NukkitUtils.isLoadedWorld(a[3])) throw PositionInformationConversionException(RuntimeException("不存在此坐标的世界"))
        return Position(a[0].toDouble(), a[1].toDouble(), a[2].toDouble(), Server.getInstance().getLevelByName(a[3])!!)
    } catch (e: Throwable) {
        throw PositionInformationConversionException(e)
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> Any?.cast(): T {
    return this as T
}

fun String.toLocation(): Location {
    try {
        val a = this.split(":")
        if (a.size != 6) throw PositionInformationConversionException(RuntimeException("String[]坐标信息数据不足(size!=6)"))
        if (!NukkitUtils.isLoadedWorld(a[5])) throw PositionInformationConversionException(RuntimeException("不存在此坐标的世界"))
        return Location(a[0].toDouble(), a[1].toDouble(), a[2].toDouble(), a[3].toDouble(), a[4].toDouble(), Server.getInstance().getLevelByName(a[5])!!)
    } catch (e: Throwable) {
        throw PositionInformationConversionException(e)
    }
}

fun Location.asString(): String = "${this.floorX}:${this.floorY}:${this.floorZ}:${this.yaw.toInt()}:${this.pitch.toInt()}:${this.level.name}"

fun Position.asString(): String = "${this.floorX}:${this.floorY}:${this.floorZ}:${this.level.name}"

fun String.toLevel(): Level {
    if (!NukkitUtils.isLoadedWorld(this)) throw PositionInformationConversionException(RuntimeException("不存在此世界:$this"))
    return Server.getInstance().getLevelByName(this)
}

object NukkitUtils {
    @JvmStatic
    var floatingTextMap = HashMap<String, FloatingTextData>()

    @JvmStatic
    fun addFloatingText(title: String, text: String, pos: Position): Long {
        val eid = Entity.entityCount++
        addFloatingText(eid, "eid_$eid", title, text, pos)
        return eid
    }

    @JvmStatic
    fun addFloatingText(id: String, title: String, text: String, pos: Position) = addFloatingText(Entity.entityCount++, id, title, text, pos)

    @JvmStatic
    private fun addFloatingText(eid: Long, id: String, title: String, text: String, pos: Position) {
        val pk = AddEntityPacket()
        pk.entityUniqueId = eid
        pk.entityRuntimeId = eid
        pk.type = 64
        pk.speedX = 0.0f
        pk.speedY = 0.0f
        pk.speedZ = 0.0f
        pk.yaw = 0.0f
        pk.pitch = 0.0f
        pk.x = pos.x.toFloat()
        pk.y = pos.y.toFloat()
        pk.z = pos.z.toFloat()
        val flags = 114688L
        pk.metadata = EntityMetadata().putLong(0, flags).putString(4, title + "\n" + text)
        Server.broadcastPacket(Server.getInstance().onlinePlayers.values.filter { it.level == pos.level }, pk)
        floatingTextMap[id] = FloatingTextData(id, eid, pos, title, text, pk)
    }

    @JvmStatic
    fun setFloatingText(eid: Long, title: String, text: String) = setFloatingText("eid_$eid", title, text)

    @JvmStatic
    fun setFloatingText(id: String, title: String, text: String) {
        if (!floatingTextMap.containsKey(id)) return
        val npk = SetEntityDataPacket()
        val flags = 114688L
        npk.metadata = EntityMetadata().putLong(0, flags).putString(4, title + "\n" + text)
        val data = floatingTextMap[id]!!
        npk.eid = data.eid
        Server.broadcastPacket(Server.getInstance().onlinePlayers.values.filter { it.level == data.pos.level }, npk)
        data.addEntityPacket.metadata = EntityMetadata().putLong(0, flags).putString(4, title + "\n" + text)
        floatingTextMap[id] = data
    }

    @JvmStatic
    fun removeFloatingText(id: String) {
        if (!floatingTextMap.containsKey(id)) return
        val data = floatingTextMap[id]!!
        val pk = RemoveEntityPacket()
        pk.eid = data.eid
        Server.broadcastPacket(Server.getInstance().onlinePlayers.values, pk)
        floatingTextMap.remove(data.id)
    }

    @JvmStatic
    fun removeFloatingText(eid: Long) = removeFloatingText("eid_$eid")

    @JvmStatic
    fun isLoadedWorld(name: String): Boolean {
        if (!Server.getInstance().isLevelLoaded(name)) {
            loadWorld(name)
            return Server.getInstance().isLevelLoaded(name)
        }
        return true
    }

    @JvmStatic
    fun loadWorld(name: String) {
        Server.getInstance().loadLevel(name)
    }
}