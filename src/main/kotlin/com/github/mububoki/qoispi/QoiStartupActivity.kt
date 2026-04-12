package com.github.mububoki.qoispi

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import javax.imageio.ImageIO
import javax.imageio.spi.IIORegistry
import javax.swing.SwingUtilities

class QoiStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
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
