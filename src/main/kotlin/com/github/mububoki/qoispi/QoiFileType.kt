package com.github.mububoki.qoispi

import com.intellij.openapi.fileTypes.FileType
import javax.swing.Icon

class QoiFileType : FileType {

    companion object {
        @JvmStatic
        val INSTANCE = QoiFileType()
    }

    override fun getName(): String = "QOI"
    override fun getDescription(): String = "QOI image file"
    override fun getDefaultExtension(): String = "qoi"
    override fun getIcon(): Icon? = null
    override fun isBinary(): Boolean = true
    override fun isReadOnly(): Boolean = true
}
