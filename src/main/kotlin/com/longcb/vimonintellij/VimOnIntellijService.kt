package com.longcb.vimonintellij

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.VirtualFileManagerListener
import com.longcb.vimonintellij.intellij.listeners.CaretEventsListener
import com.longcb.vimonintellij.intellij.listeners.FileEventsListener
import com.longcb.vimonintellij.intellij.listeners.VirtualFileEventsListener
import com.longcb.vimonintellij.neovim.NeovimApi
import com.longcb.vimonintellij.neovim.SocketConnection

@Service
class VimOnIntellijService {
    private val logger = Logger.getInstance(this.javaClass)

    private val neovimApi: NeovimApi by lazy {
        val connection = SocketConnection("localhost", 6666)
        NeovimApi(connection)
    }

    fun listenToFileEvents(project: Project) {
        project.messageBus.connect().subscribe(
            topic = FileEditorManagerListener.FILE_EDITOR_MANAGER,
            handler = FileEventsListener(neovimApi),
        )
        project.messageBus.connect().subscribe(
            topic = VirtualFileManager.VFS_CHANGES,
            handler = VirtualFileEventsListener(neovimApi),
        )
        EditorFactory.getInstance().eventMulticaster.addCaretListener(
            CaretEventsListener(neovimApi),
            neovimApi,
        )
    }

    fun getVimInfo() {
        logger.info("Start to get vim info.")

        neovimApi.getVimInfo().thenAccept {
            logger.info("Finished to get vim info. $it")
        }
    }

    private fun showMessageDialog(project: Project, title: String, content: String) {
        Messages.showMessageDialog(project, content, title, Messages.getInformationIcon())
    }
}

