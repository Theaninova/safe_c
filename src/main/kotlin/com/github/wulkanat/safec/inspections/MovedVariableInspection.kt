package com.github.wulkanat.safec.inspections

import com.github.wulkanat.safec.VariableOwnershipStatus
import com.github.wulkanat.safec.extensions.findAllDeep
import com.github.wulkanat.safec.extensions.findFirstParent
import com.github.wulkanat.safec.extensions.forEachDeep
import com.github.wulkanat.safec.quickfixes.ReplaceBorrowedWithOwnedQuickFix
import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.cidr.lang.inspections.OCInspections
import com.jetbrains.cidr.lang.psi.*
import com.jetbrains.cidr.lang.psi.visitors.OCVisitor

class MovedVariableInspection : OCInspections.GeneralCpp() {
    override fun worksWithClangd() = true
    override fun isEnabledByDefault() = true
    override fun getShortName() = "MovedVariable"
    override fun getDisplayName() = "Variable used after it has been moved"
    override fun getGroupPath() = arrayOf("C/C++", "Static Memory Safety")
    override fun getDefaultLevel(): HighlightDisplayLevel = HighlightDisplayLevel.ERROR
    override fun getStaticDescription() = "The variable could have changed after it has been moved"

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : OCVisitor() {
            /*override fun visitAssignmentExpression(expression: OCAssignmentExpression?) {
                expression ?: return
                val originalElement = (if (expression.sourceExpression is OCReferenceElement)
                    expression.sourceExpression as OCReferenceElement else return).originalElement

                expression.findNextDeep { it is OCReferenceElement && it.isReferenceTo(originalElement) }?.let {
                    holder.registerProblem(it, "Referenced variable after it has been moved",
                            ReplaceBorrowedWithOwnedQuickFix())
                }

                /*val (parent, child) = expression.findFirstParent { it is OCBlockStatement }
                parent ?: return
                val index = parent.children.indexOf(child)
                parent.findAllDeep(index) {
                    it is OCReferenceElement && it.reference?.isReferenceTo(originalReference.element) == true
                }.forEach {

                }*/
            }*/

            override fun visitReferenceExpression(expression: OCReferenceExpression?) {
                val a = expression
                val reference = expression?.referenceElement ?: return
                // allow access to .raw and .borrowed
                if (expression.parent is OCQualifiedExpression) return

                val originalElement = reference.originalElement
                if (originalElement !is OCReferenceElement) return
                if (!(expression.resolvedType.name matches VariableOwnershipStatus.OWNED.pattern)) return

                val (parent, child) = expression.findFirstParent { it is OCBlockStatement }
                parent!!.forEachDeep(parent.children.indexOf(child)) {
                    if (it === reference) {
                        return@forEachDeep
                    }

                    if (it is OCReferenceElement && it.text == originalElement.text) {
                        holder.registerProblem(it, "Referenced variable after it has been moved",
                                ReplaceBorrowedWithOwnedQuickFix())
                    }
                }
            }
        }
    }
}