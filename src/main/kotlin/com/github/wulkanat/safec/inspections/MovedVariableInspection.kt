package com.github.wulkanat.safec.inspections

import com.github.wulkanat.safec.VariableOwnershipStatus
import com.github.wulkanat.safec.extensions.findFirstParent
import com.github.wulkanat.safec.extensions.forEachDeep
import com.github.wulkanat.safec.quickfixes.ReplaceExpressionQuickFix
import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.cidr.lang.inspections.OCInspections
import com.jetbrains.cidr.lang.psi.OCAssignmentExpression
import com.jetbrains.cidr.lang.psi.OCBlockStatement
import com.jetbrains.cidr.lang.psi.OCQualifiedExpression
import com.jetbrains.cidr.lang.psi.OCReferenceElement
import com.jetbrains.cidr.lang.psi.OCReferenceExpression
import com.jetbrains.cidr.lang.psi.visitors.OCVisitor

class MovedVariableInspection : OCInspections.GeneralCpp() {
    override fun worksWithClangd() = true
    override fun isEnabledByDefault() = true
    override fun getShortName() = "MovedVariable"
    override fun getDisplayName() = "Variable used after it has been moved"
    override fun getGroupPath() = arrayOf("C/C++", "Static Memory Safety")
    override fun getDefaultLevel(): HighlightDisplayLevel = HighlightDisplayLevel.ERROR
    override fun getStaticDescription() = "The variable could have changed after it has been moved"

    /*var movedVariableCheck = true
    var enforceVariableDeletion = true

    override fun createOptionsPanel(): JComponent? {
        return JPanel(VerticalFlowLayout()).apply {
            add(checkBox("Disallow usage of variable after it has been moved") { movedVariableCheck = it })
            add(checkBox("Enforce deletion of variables that haven't been moved") { enforceVariableDeletion = it })
        }
    }*/

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : OCVisitor() {
            override fun visitReferenceExpression(expression: OCReferenceExpression?) {
                val a = expression
                val reference = expression?.referenceElement ?: return
                // allow access to .raw and .borrowed
                if (expression.parent is OCQualifiedExpression) return
                // exclude left hand assignments
                if (expression.parent is OCAssignmentExpression &&
                    (expression.parent as OCAssignmentExpression).receiverExpression == expression
                ) return

                val originalElement = reference.originalElement
                if (originalElement !is OCReferenceElement) return
                if (!(expression.resolvedType.name matches VariableOwnershipStatus.OWNED.pattern)) return

                val (parent, child) = expression.findFirstParent { it is OCBlockStatement }
                parent!!.forEachDeep(parent.children.indexOf(child)) { element ->
                    if (element is OCReferenceElement) {
                        if (element === reference) {
                            return@forEachDeep
                        }

                        if (element.text == originalElement.text) {
                            val elmParent = expression.parent
                            if (elmParent is OCAssignmentExpression) {
                                holder.registerProblem(
                                    element, "Referenced variable after it has been moved or deleted",
                                    ReplaceExpressionQuickFix(element, elmParent.receiverExpression)
                                )
                            }
                            holder.registerProblem(element, "Referenced variable after it has been moved or deleted")
                        }
                    }
                }
            }
        }
    }
}
