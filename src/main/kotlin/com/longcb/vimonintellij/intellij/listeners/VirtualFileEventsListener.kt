package com.longcb.vimonintellij.intellij.listeners

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.longcb.vimonintellij.neovim.NeovimApi

class VirtualFileEventsListener(private val neovimApi: NeovimApi) : BulkFileListener {
    private val logger = Logger.getInstance(javaClass)

    override fun after(events: MutableList<out VFileEvent>) {
        super.after(events)

        logger.info("Reload current buffer")
        neovimApi.reloadCurrentBuffer()
    }
}

