package com.github.mububoki.qoispi

import java.awt.image.BufferedImage
import java.io.InputStream

object QoiDecoder {

    private const val MAGIC = 0x716F6966 // "qoif"
    private const val OP_INDEX: Int = 0x00
    private const val OP_DIFF: Int = 0x40
    private const val OP_LUMA: Int = 0x80
    private const val OP_RUN: Int = 0xC0
    private const val OP_RGB: Int = 0xFE
    private const val OP_RGBA: Int = 0xFF
    private const val MASK_2: Int = 0xC0

    data class QoiHeader(
        val width: Int,
        val height: Int,
        val channels: Int,
        val colorspace: Int,
    )

    fun readHeader(input: InputStream): QoiHeader {
        val header = input.readNBytes(14)
        if (header.size < 14) throw IllegalArgumentException("Invalid QOI header: too short")

        val magic = header.readInt(0)
        if (magic != MAGIC) throw IllegalArgumentException("Invalid QOI magic: ${magic.toString(16)}")

        val width = header.readInt(4)
        val height = header.readInt(8)
        val channels = header[12].toInt() and 0xFF
        val colorspace = header[13].toInt() and 0xFF

        if (width <= 0 || height <= 0) throw IllegalArgumentException("Invalid dimensions: ${width}x${height}")
        if (channels !in 3..4) throw IllegalArgumentException("Invalid channels: $channels")

        return QoiHeader(width, height, channels, colorspace)
    }

    fun decode(input: InputStream): BufferedImage {
        val header = readHeader(input)
        val pixelCount = header.width * header.height

        val imageType = if (header.channels == 4) BufferedImage.TYPE_INT_ARGB else BufferedImage.TYPE_INT_RGB
        val image = BufferedImage(header.width, header.height, imageType)

        val index = IntArray(64)
        var r = 0
        var g = 0
        var b = 0
        var a = 255

        val data = input.readAllBytes()
        var pos = 0
        var pixelIndex = 0

        while (pixelIndex < pixelCount && pos < data.size) {
            val b1 = data[pos++].toInt() and 0xFF

            when {
                b1 == OP_RGB -> {
                    r = data[pos++].toInt() and 0xFF
                    g = data[pos++].toInt() and 0xFF
                    b = data[pos++].toInt() and 0xFF
                }
                b1 == OP_RGBA -> {
                    r = data[pos++].toInt() and 0xFF
                    g = data[pos++].toInt() and 0xFF
                    b = data[pos++].toInt() and 0xFF
                    a = data[pos++].toInt() and 0xFF
                }
                (b1 and MASK_2) == OP_INDEX -> {
                    val idx = b1 and 0x3F
                    val pixel = index[idx]
                    r = (pixel shr 24) and 0xFF
                    g = (pixel shr 16) and 0xFF
                    b = (pixel shr 8) and 0xFF
                    a = pixel and 0xFF
                }
                (b1 and MASK_2) == OP_DIFF -> {
                    r = (r + ((b1 shr 4) and 0x03) - 2) and 0xFF
                    g = (g + ((b1 shr 2) and 0x03) - 2) and 0xFF
                    b = (b + (b1 and 0x03) - 2) and 0xFF
                }
                (b1 and MASK_2) == OP_LUMA -> {
                    val b2 = data[pos++].toInt() and 0xFF
                    val vg = (b1 and 0x3F) - 32
                    r = (r + vg - 8 + ((b2 shr 4) and 0x0F)) and 0xFF
                    g = (g + vg) and 0xFF
                    b = (b + vg - 8 + (b2 and 0x0F)) and 0xFF
                }
                (b1 and MASK_2) == OP_RUN -> {
                    var run = (b1 and 0x3F) + 1
                    val argb = (a shl 24) or (r shl 16) or (g shl 8) or b
                    while (run-- > 0 && pixelIndex < pixelCount) {
                        val x = pixelIndex % header.width
                        val y = pixelIndex / header.width
                        image.setRGB(x, y, argb)
                        pixelIndex++
                    }
                    val hashIndex = (r * 3 + g * 5 + b * 7 + a * 11) % 64
                    index[hashIndex] = (r shl 24) or (g shl 16) or (b shl 8) or a
                    continue
                }
                else -> throw IllegalArgumentException("Invalid QOI tag: 0x${b1.toString(16)}")
            }

            val hashIndex = (r * 3 + g * 5 + b * 7 + a * 11) % 64
            index[hashIndex] = (r shl 24) or (g shl 16) or (b shl 8) or a

            val x = pixelIndex % header.width
            val y = pixelIndex / header.width
            val argb = (a shl 24) or (r shl 16) or (g shl 8) or b
            image.setRGB(x, y, argb)
            pixelIndex++
        }

        if (pos + 8 > data.size) {
            throw IllegalArgumentException("QOI: missing end marker")
        }
        val endMarker = ByteArray(8)
        System.arraycopy(data, pos, endMarker, 0, 8)
        val expected = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 1)
        if (!endMarker.contentEquals(expected)) {
            throw IllegalArgumentException("QOI: invalid end marker")
        }

        return image
    }

    private fun ByteArray.readInt(offset: Int): Int {
        return ((this[offset].toInt() and 0xFF) shl 24) or
                ((this[offset + 1].toInt() and 0xFF) shl 16) or
                ((this[offset + 2].toInt() and 0xFF) shl 8) or
                (this[offset + 3].toInt() and 0xFF)
    }
}
