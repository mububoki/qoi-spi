package com.github.mububoki.qoispi

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.impl.FileTypeOverrider
import com.intellij.openapi.vfs.VirtualFile
import org.intellij.images.fileTypes.impl.ImageFileType

class QoiFileTypeOverrider : FileTypeOverrider {
    override fun getOverriddenFileType(file: VirtualFile): FileType? {
        if (file.extension?.lowercase() == "qoi") {
            return ImageFileType.INSTANCE
        }
        return null
    }
}
