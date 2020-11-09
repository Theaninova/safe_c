package com.github.wulkanat.safec.inspections

import com.github.wulkanat.safec.extensions.findFirstParent
import com.github.wulkanat.safec.extensions.reverseForEachDeepFlat
import com.github.wulkanat.safec.quickfixes.InsertDeleteQuickFix
import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.cidr.lang.inspections.OCInspections
import com.jetbrains.cidr.lang.psi.OCAssignmentExpression
import com.jetbrains.cidr.lang.psi.OCDeclaration
import com.jetbrains.cidr.lang.psi.OCFunctionDeclaration
import com.jetbrains.cidr.lang.psi.OCMacroCall
import com.jetbrains.cidr.lang.psi.OCMacroCallArgument
import com.jetbrains.cidr.lang.psi.OCReturnStatement
import com.jetbrains.cidr.lang.psi.visitors.OCVisitor

class EnforceDeletionWhenOwnedInspection : OCInspections.GeneralCpp() {
    override fun worksWithClangd() = true
    override fun isEnabledByDefault() = true
    override fun getShortName() = "EnforceDeletionWhenOwned"
    override fun getDisplayName() = "Enforce deletion of owned variables"
    override fun getGroupPath() = arrayOf("C/C++", "Static Memory Safety")
    override fun getDefaultLevel(): HighlightDisplayLevel = HighlightDisplayLevel.ERROR
    override fun getStaticDescription() = "When a variable is owned by a scope, enforce deletion at the end"

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : OCVisitor() {
            override fun visitReturnStatement(statement: OCReturnStatement?) {
                statement ?: return

                val variables = mutableListOf<String>()
                val variablesWithMissingDelete = mutableListOf<String>()

                val parent = statement.findFirstParent { it is OCFunctionDeclaration }.first
                statement.reverseForEachDeepFlat(parent) { element ->
                    when (element) {
                        is OCDeclaration -> {
                            element.declarators.forEach { declarator ->
                                if (!variables.removeIf { it == declarator.firstChild.text })
                                    variablesWithMissingDelete += declarator.firstChild.text
                            }
                        }
                        is OCMacroCall -> {
                            variables += element.children.filterIsInstance<OCMacroCallArgument>().first().text
                        }
                        is OCAssignmentExpression -> { element.sourceExpression?.text?.let { variables += it } }
                    }
                }

                variablesWithMissingDelete.forEach {
                    holder.registerProblem(
                        statement, "Missing delete for \"${it}\"",
                        InsertDeleteQuickFix(it)
                    )
                }
            }

            // TODO: Top level variables
        }
    }
}
