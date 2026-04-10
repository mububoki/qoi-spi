package com.github.mububoki.qoispi

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import java.beans.PropertyChangeListener
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JScrollPane
import javax.swing.SwingConstants

class QoiFileEditor(private val file: VirtualFile) : UserDataHolderBase(), FileEditor {

    private val _component: JComponent by lazy {
        try {
            val image = QoiDecoder.decode(file.inputStream)
            val label = JLabel(ImageIcon(image))
            label.horizontalAlignment = SwingConstants.CENTER
            label.verticalAlignment = SwingConstants.CENTER
            JScrollPane(label)
        } catch (e: Exception) {
            JLabel("Failed to decode QOI image: ${e.message}").apply {
                horizontalAlignment = SwingConstants.CENTER
            }
        }
    }

    override fun getComponent(): JComponent = _component
    override fun getPreferredFocusedComponent(): JComponent = _component
    override fun getName(): String = "QOI Image"
    override fun setState(state: FileEditorState) {}
    override fun isModified(): Boolean = false
    override fun isValid(): Boolean = file.isValid
    override fun addPropertyChangeListener(listener: PropertyChangeListener) {}
    override fun removePropertyChangeListener(listener: PropertyChangeListener) {}
    override fun dispose() {}
    override fun getFile(): VirtualFile = file
}
