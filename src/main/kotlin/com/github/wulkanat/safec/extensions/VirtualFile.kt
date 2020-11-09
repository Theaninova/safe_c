package com.github.wulkanat.safec.extensions

import com.intellij.psi.PsiFile

fun PsiFile.isCFile(): Boolean {
    return when (fileType.defaultExtension.toLowerCase()) {
        "c", "h" -> true
        else -> false
    }
}