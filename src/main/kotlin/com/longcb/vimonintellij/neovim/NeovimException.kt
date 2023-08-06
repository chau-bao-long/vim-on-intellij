package com.longcb.vimonintellij.neovim

import com.fasterxml.jackson.databind.JsonNode
import org.msgpack.core.MessagePacker
import java.io.IOException
import java.util.Optional

class NeovimException(val errorCode: Long, errorMessage: String?) : RuntimeException(errorMessage) {

    @Throws(IOException::class)
    fun serialize(packer: MessagePacker) {
        packer.packArrayHeader(2)
        packer.packLong(errorCode)
        packer.packString(message)
    }

    companion object {
        fun parseError(node: JsonNode): Optional<NeovimException> {
            if (node.isNull) {
                return Optional.empty()
            }
            if (node.isArray && node.size() == 2) {
                if (node[0].isIntegralNumber) {
                    val errorCode = node[0].asLong()
                    val errorMessage: String = getText(node[1])
                    return Optional.of(NeovimException(errorCode, errorMessage))
                }
            }
            return Optional.of(NeovimException(-1, "Unknown Error: $node"))
        }
    }
}