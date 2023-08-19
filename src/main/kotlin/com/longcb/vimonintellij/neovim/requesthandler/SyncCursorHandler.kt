package com.longcb.vimonintellij.neovim.requesthandler

import com.fasterxml.jackson.databind.JsonNode
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.longcb.vimonintellij.intellij.utils.getProject
import com.longcb.vimonintellij.intellij.utils.getVirtualFile

class SyncCursorHandler : IRequestHandler {
    private val logger = Logger.getInstance(javaClass)

    override val method = "request_sync_cursor"

    override fun handle(data: JsonNode): Any? {
        logger.info("Receive request sync cursor: $data")

        val filePath = data[0]["file"].asText()
        val offset = data[0]["offset"].intValue()

        val project = getProject()
        val virtualFile = getVirtualFile(filePath) ?: return null

        ApplicationManager.getApplication().invokeLater {
            FileEditorManager
                .getInstance(project)
                .openTextEditor(
                    OpenFileDescriptor(project, virtualFile, offset),
                    true,
                )
        }

        return null
    }
}

