package com.longcb.vimonintellij.intellij.listeners

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile
import com.longcb.vimonintellij.neovim.NeovimApi

class FileEventsListener(private val neovimApi: NeovimApi) : FileEditorManagerListener {
    private val logger = Logger.getInstance(javaClass)

    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        super.fileOpened(source, file)

        logger.info("open file ${file.path}")

        neovimApi.openFile(file.path)
    }
}

