package com.github.mububoki.qoispi

import java.util.Locale
import javax.imageio.ImageReader
import javax.imageio.spi.ImageReaderSpi
import javax.imageio.stream.ImageInputStream

class QoiImageReaderSpi : ImageReaderSpi(
    "mububoki",                                    // vendorName
    "0.1.0",                                       // version
    arrayOf("qoi", "QOI"),                         // names
    arrayOf("qoi"),                                // suffixes
    arrayOf("image/qoi"),                          // MIMETypes
    "com.github.mububoki.qoispi.QoiImageReader",   // readerClassName
    arrayOf<Class<*>>(ImageInputStream::class.java), // inputTypes
    null,                                          // writerSpiNames
    false,                                         // supportsStandardStreamMetadataFormat
    null,                                          // nativeStreamMetadataFormatName
    null,                                          // nativeStreamMetadataFormatClassName
    null,                                          // extraStreamMetadataFormatNames
    null,                                          // extraStreamMetadataFormatClassNames
    false,                                         // supportsStandardImageMetadataFormat
    null,                                          // nativeImageMetadataFormatName
    null,                                          // nativeImageMetadataFormatClassName
    null,                                          // extraImageMetadataFormatNames
    null,                                          // extraImageMetadataFormatClassNames
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
