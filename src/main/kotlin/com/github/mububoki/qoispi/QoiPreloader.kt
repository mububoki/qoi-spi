package com.github.mububoki.qoispi

import com.intellij.openapi.application.PreloadingActivity
import com.intellij.openapi.diagnostic.Logger
import javax.imageio.ImageIO
import javax.imageio.spi.IIORegistry

class QoiPreloader : PreloadingActivity() {
    private val log = Logger.getInstance(QoiPreloader::class.java)

    override fun preload() {
        log.info("QoiPreloader: registering QOI ImageReaderSpi")
        val registry = IIORegistry.getDefaultInstance()
        registry.registerServiceProvider(QoiImageReaderSpi())
        log.info("QoiPreloader: registered. ImageIO suffixes: ${ImageIO.getReaderFileSuffixes().toList()}")
    }
}
