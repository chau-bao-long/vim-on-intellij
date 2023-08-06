package com.longcb.vimonintellij

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.project.Project
import com.longcb.vimonintellij.neovim.NeovimApi
import com.longcb.vimonintellij.neovim.SocketConnection

@Service
class VimOnIntellijService {
    private val logger = Logger.getInstance(this.javaClass)

    private val neovimApi: NeovimApi by lazy {
        val connection = SocketConnection("localhost", 6666)
        NeovimApi(connection)
    }

    private fun showMessageDialog(project: Project, title: String, content: String) {
        Messages.showMessageDialog(project, content, title, Messages.getInformationIcon())
    }

    fun getVimInfo() {
        logger.info("Start to get vim info.")

        neovimApi.getVimInfo().thenAccept {
            logger.info("Finished to get vim info. $it")
        }
    }
}

