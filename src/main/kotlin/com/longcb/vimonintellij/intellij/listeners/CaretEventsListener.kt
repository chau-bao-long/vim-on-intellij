package com.longcb.vimonintellij.intellij.listeners

import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.longcb.vimonintellij.neovim.NeovimApi

class CaretEventsListener(private val neovimApi: NeovimApi) : CaretListener {

    override fun caretPositionChanged(event: CaretEvent) {
        val offset = event.caret?.caretModel?.offset ?: 0

        neovimApi.moveCursor(offset)
    }
}

