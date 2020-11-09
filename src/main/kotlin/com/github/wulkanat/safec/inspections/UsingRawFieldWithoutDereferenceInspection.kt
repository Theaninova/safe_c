package com.github.wulkanat.safec.inspections

import com.github.wulkanat.safec.VariableOwnershipStatus
import com.github.wulkanat.safec.extensions.findFirstParent
import com.github.wulkanat.safec.quickfixes.DereferenceQuickFix
import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.cidr.lang.inspections.OCInspections
import com.jetbrains.cidr.lang.psi.OCParenthesizedExpression
import com.jetbrains.cidr.lang.psi.OCQualifiedExpression
import com.jetbrains.cidr.lang.psi.OCUnaryExpression
import com.jetbrains.cidr.lang.psi.visitors.OCVisitor

class UsingRawFieldWithoutDereferenceInspection : OCInspections.GeneralCpp() {
    override fun worksWithClangd() = true
    override fun isEnabledByDefault() = true
    override fun getShortName() = "DisallowRawFieldAccessWithoutDereference"
    override fun getDisplayName() = "Disallow usage of .raw field without dereference"
    override fun getGroupPath() = arrayOf("C/C++", "Static Memory Safety")
    override fun getDefaultLevel(): HighlightDisplayLevel = HighlightDisplayLevel.ERROR
    override fun getStaticDescription() = "Accessing the pointer directly works around the memory safety and is probably a mistake."

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : OCVisitor() {
            override fun visitQualifiedExpression(expression: OCQualifiedExpression?) {
                expression ?: return

                if (expression.qualifier.resolvedType.name matches VariableOwnershipStatus.ALL.pattern && expression.symbolName == "raw") {
                    val expr = expression.findFirstParent { it !is OCParenthesizedExpression }.first
                    if (expr !is OCUnaryExpression || expr.operationName != "*") {
                        holder.registerProblem(expression, "Using raw field without dereference", DereferenceQuickFix())
                    }
                }
            }
        }
    }
}
