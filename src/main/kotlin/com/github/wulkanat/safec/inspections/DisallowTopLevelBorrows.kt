package com.github.wulkanat.safec.inspections

import com.github.wulkanat.safec.VariableOwnershipStatus
import com.github.wulkanat.safec.extensions.checkBox
import com.github.wulkanat.safec.quickfixes.ReplaceBorrowedWithOwnedQuickFix
import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.ui.VerticalFlowLayout
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import com.jetbrains.cidr.lang.inspections.OCInspections
import com.jetbrains.cidr.lang.psi.OCDeclaration
import com.jetbrains.cidr.lang.psi.visitors.OCVisitor
import javax.swing.JComponent
import javax.swing.JPanel

class DisallowTopLevelBorrows : OCInspections.GeneralCpp() {
    override fun worksWithClangd() = true
    override fun isEnabledByDefault() = true
    override fun getShortName() = "DisallowTopLevelBorrows"
    override fun getDisplayName() = "Disallow top-level borrows"
    override fun getGroupPath() = arrayOf("C/C++", "Static Memory Safety")
    override fun getDefaultLevel(): HighlightDisplayLevel = HighlightDisplayLevel.ERROR
    override fun getStaticDescription() = "Top level borrows would compromise memory safety, as they are still there after the function is exited"

    var disallowTopLevelBorrows = true

    override fun createOptionsPanel(): JComponent? {
        return JPanel(VerticalFlowLayout()).apply {
            add(checkBox("Disallow top-level borrows") { disallowTopLevelBorrows = it })
        }
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : OCVisitor() {
            override fun visitDeclaration(declaration: OCDeclaration?) {
                declaration ?: return
                if (disallowTopLevelBorrows
                        && declaration.type.name matches VariableOwnershipStatus.BORROWED.pattern
                        && declaration.parent is PsiFile) {
                    holder.registerProblem(declaration, "Top-level borrows compromise memory safety",
                            ReplaceBorrowedWithOwnedQuickFix())
                }
            }
        }
    }
}