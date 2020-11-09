package com.github.wulkanat.safec.extensions

import com.github.wulkanat.safec.VariableOwnershipStatus
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.jetbrains.cidr.lang.parser.OCTokenTypes

fun PsiElement.getOwnershipStatus(): VariableOwnershipStatus? {
    if (elementType == OCTokenTypes.IDENTIFIER) {
        for (status in VariableOwnershipStatus.values()) {
            if (text matches status.pattern) {
                return status
            }
        }
    }

    return null
}