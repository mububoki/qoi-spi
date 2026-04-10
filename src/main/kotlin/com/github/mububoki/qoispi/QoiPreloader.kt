package com.github.mububoki.qoispi

import com.intellij.openapi.application.PreloadingActivity
import javax.imageio.ImageIO
import javax.imageio.spi.IIORegistry
import javax.swing.SwingUtilities

class QoiPreloader : PreloadingActivity() {
    override fun preload() {
        registerSpi()
        SwingUtilities.invokeLater { registerSpi() }
    }

    companion object {
        fun registerSpi() {
            val originalCL = Thread.currentThread().contextClassLoader
            try {
                Thread.currentThread().contextClassLoader = QoiImageReaderSpi::class.java.classLoader
                IIORegistry.getDefaultInstance().registerServiceProvider(QoiImageReaderSpi())
                ImageIO.scanForPlugins()
            } finally {
                Thread.currentThread().contextClassLoader = originalCL
            }
        }
    }
}
