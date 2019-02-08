package net.mcpes.summit.hhm.base.utils

import cn.nukkit.Server
import cn.nukkit.level.Level
import cn.nukkit.level.Location
import cn.nukkit.level.Position
import net.mcpes.summit.hhm.base.error.PositionInformationConversionException

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