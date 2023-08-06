package com.longcb.vimonintellij.neovim

import com.fasterxml.jackson.databind.JsonNode
import java.io.IOException

fun getText(node: JsonNode): String {
    return if (node.isBinary) {
        try {
            String(node.binaryValue())
        } catch (e: IOException) {
            throw AssertionError(e)
        }
    } else node.asText()
}

fun formatJsonNode(node: JsonNode, builder: StringBuilder): StringBuilder {
    if (node.isArray) {
        builder.append('[')
        if (node.size() > 0) {
            formatJsonNode(node.get(0), builder)
            for (i in 1 until node.size()) {
                builder.append(", ")
                formatJsonNode(node.get(i), builder)
            }
        }
        builder.append(']')
    } else if (node.isBinary) {
        try {
            builder.append(String(node.binaryValue()))
        } catch (e: IOException) {
            builder.append(node.toString())
        }
    } else {
        builder.append(node.toString())
    }
    return builder
}

fun formatJsonNode(node: JsonNode): String {
    return formatJsonNode(node, StringBuilder()).toString()
}
