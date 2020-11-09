package com.github.wulkanat.safec.extensions

import com.github.wulkanat.safec.VariableOwnershipStatus
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
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

fun PsiElement.findFirstParent(condition: (child: PsiElement) -> Boolean): Pair<PsiElement?/*Parent*/, PsiElement/*Child*/> {
    var newParent: PsiElement? = parent ?: return Pair(null, this)
    var newChild: PsiElement? = null
    while (newParent != null && !condition(newParent)) {
        newChild = newParent
        newParent = newParent.parent
    }

    return Pair(newParent, newChild ?: this)
}

/*
 * Find next element in the tree deep that matches condition.
 *
fun PsiElement.findNextDeep(condition: (child: PsiElement) -> Boolean): PsiElement? {
    var currentChild = nextSibling ?: parent
    var depth = 0

    while (currentChild != null && currentChild !is PsiFile) {
        if (condition(currentChild)) return currentChild

        currentChild = currentChild.firstChild ?: currentChild.nextSibling ?: currentChild.parent
    }

    return null
}

// un PsiElement.forEachNext

fun PsiElement.findAllNextDeep(condition: (child: PsiElement) -> Boolean, breakCondition: (parent: PsiElement) -> Boolean): List<PsiElement> {
    val out = mutableListOf<PsiElement>()

    findAllNextDeep(condition, out)

    return out
}

private fun PsiElement.findAllNextDeep(condition: (child: PsiElement) -> Boolean, list: MutableList<PsiElement>) {
    children.forEach {
        if (condition(it)) list.add(it)
        it.findAllNextDeep(condition, list)
    }
}*/

fun PsiElement.reverseForEachDeepFlat(until: PsiElement? = null, executor: (child: PsiElement) -> Unit) {
    var currentChild = prevSibling ?: parent

    while (currentChild != until) {
        executor(currentChild)
        currentChild = currentChild.prevSibling ?: currentChild.parent
    }
}

fun PsiElement.forEachDeep(startAtElement: Int = 0, until: PsiElement? = null, executor: (child: PsiElement) -> Unit): Boolean {
    for (i in startAtElement until children.size) {
        val element = children[i]
        if (element === until) return true
        executor(element)
        if (element.forEachDeep(0, until, executor)) return true
    }

    return false
}

fun PsiElement.findAllDeep(startAtElement: Int = 0, condition: (child: PsiElement) -> Boolean): List<PsiElement> {
    val out = mutableListOf<PsiElement>()
    findAllDeep(startAtElement, condition, out)
    return out
}

private fun PsiElement.findAllDeep(startAtElement: Int, condition: (child: PsiElement) -> Boolean, list: MutableList<PsiElement>) {
    for (i in startAtElement until children.size) {
        val element = children[i]
        if (condition(element)) {
            list.add(element)
        }
        element.findAllDeep(0, condition, list)
    }
}