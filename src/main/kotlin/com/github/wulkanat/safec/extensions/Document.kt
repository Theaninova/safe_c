package com.github.wulkanat.safec.extensions

import com.intellij.openapi.editor.Document

fun Document.hasLines(): Document? {
    return if (lineCount > 0) this else null
}