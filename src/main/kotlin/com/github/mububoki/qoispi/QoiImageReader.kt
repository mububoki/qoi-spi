package com.github.mububoki.qoispi

import java.awt.image.BufferedImage
import javax.imageio.ImageReadParam
import javax.imageio.ImageReader
import javax.imageio.metadata.IIOMetadata
import javax.imageio.spi.ImageReaderSpi
import javax.imageio.stream.ImageInputStream

class QoiImageReader(originatingProvider: ImageReaderSpi) : ImageReader(originatingProvider) {

    private var header: QoiDecoder.QoiHeader? = null

    override fun getNumImages(allowSearch: Boolean): Int = 1

    override fun getWidth(imageIndex: Int): Int {
        checkIndex(imageIndex)
        return readHeader().width
    }

    override fun getHeight(imageIndex: Int): Int {
        checkIndex(imageIndex)
        return readHeader().height
    }

    override fun getImageTypes(imageIndex: Int): Iterator<javax.imageio.ImageTypeSpecifier> {
        checkIndex(imageIndex)
        val h = readHeader()
        val imageType = if (h.channels == 4) BufferedImage.TYPE_INT_ARGB else BufferedImage.TYPE_INT_RGB
        return listOf(javax.imageio.ImageTypeSpecifier.createFromBufferedImageType(imageType)).iterator()
    }

    override fun getStreamMetadata(): IIOMetadata? = null

    override fun getImageMetadata(imageIndex: Int): IIOMetadata? = null

    override fun read(imageIndex: Int, param: ImageReadParam?): BufferedImage {
        checkIndex(imageIndex)
        val stream = input as ImageInputStream
        stream.seek(0)
        val bytes = ByteArray(stream.length().toInt())
        stream.readFully(bytes)
        return QoiDecoder.decode(bytes.inputStream())
    }

    private fun readHeader(): QoiDecoder.QoiHeader {
        if (header == null) {
            val stream = input as ImageInputStream
            stream.seek(0)
            val headerBytes = ByteArray(14)
            stream.readFully(headerBytes)
            header = QoiDecoder.readHeader(headerBytes.inputStream())
        }
        return header!!
    }

    private fun checkIndex(imageIndex: Int) {
        if (imageIndex != 0) throw IndexOutOfBoundsException("imageIndex must be 0")
    }

    override fun setInput(input: Any?, seekForwardOnly: Boolean, ignoreMetadata: Boolean) {
        super.setInput(input, seekForwardOnly, ignoreMetadata)
        header = null
    }
}
