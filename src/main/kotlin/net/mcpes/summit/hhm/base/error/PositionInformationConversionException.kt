package net.mcpes.summit.hhm.base.error

class PositionInformationConversionException(e: Throwable, message: String = "在变换坐标信息时遇到错误,错误信息:${e.message}") : RuntimeException(message)