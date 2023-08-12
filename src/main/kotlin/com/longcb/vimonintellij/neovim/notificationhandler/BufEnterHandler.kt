package com.longcb.vimonintellij.neovim.notificationhandler

import com.fasterxml.jackson.databind.JsonNode
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileEditorManager
import com.longcb.vimonintellij.intellij.utils.getProject
import com.longcb.vimonintellij.intellij.utils.getVirtualFile

internal class BufEnterHandler : INotificationHandler {
    private val logger = Logger.getInstance(javaClass)

    override val method = "notify_buf_enter"

    override fun handle(data: JsonNode) {
        val filePath = data[0]["path"].asText()

        logger.info("Receive file $filePath")

        val project = getProject()
        val virtualFile = getVirtualFile(filePath) ?: return

        logger.info("Go to $filePath")

        ApplicationManager.getApplication().invokeLater {
            FileEditorManager
                .getInstance(project)
                .openFile(virtualFile, true)
        }
    }
}
