package com.github.mububoki.qoispi

import java.util.Locale
import javax.imageio.ImageReader
import javax.imageio.spi.ImageReaderSpi
import javax.imageio.stream.ImageInputStream

class QoiImageReaderSpi : ImageReaderSpi(
    "mububoki",
    "0.1.0",
    arrayOf("qoi", "QOI"),
    arrayOf("qoi"),
    arrayOf("image/qoi"),
    "com.github.mububoki.qoispi.QoiImageReader",
    arrayOf<Class<*>>(ImageInputStream::class.java),
    null, false, null, null, null, null,
    false, null, null, null, null,
) {
    companion object {
        private val MAGIC = byteArrayOf(0x71, 0x6F, 0x69, 0x66) // "qoif"
    }

    override fun canDecodeInput(source: Any?): Boolean {
        if (source !is ImageInputStream) return false
        return try {
            source.mark()
            val header = ByteArray(4)
            source.readFully(header)
            source.reset()
            header.contentEquals(MAGIC)
        } catch (e: Exception) {
            false
        }
    }

    override fun createReaderInstance(extension: Any?): ImageReader {
        return QoiImageReader(this)
    }

    override fun getDescription(locale: Locale?): String {
        return "QOI (Quite OK Image) Format Reader"
    }
}
