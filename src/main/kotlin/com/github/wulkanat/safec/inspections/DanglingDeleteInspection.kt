package com.github.wulkanat.safec.inspections

import com.github.wulkanat.safec.extensions.findNext
import com.github.wulkanat.safec.extensions.firstParentBefore
import com.github.wulkanat.safec.extensions.isSafeCDelete
import com.github.wulkanat.safec.quickfixes.DeleteQuickFix
import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.cidr.lang.inspections.OCInspections
import com.jetbrains.cidr.lang.psi.OCBlockStatement
import com.jetbrains.cidr.lang.psi.OCMacroCall
import com.jetbrains.cidr.lang.psi.OCReturnStatement
import com.jetbrains.cidr.lang.psi.visitors.OCVisitor

class DanglingDeleteInspection : OCInspections.GeneralCpp() {
    override fun worksWithClangd() = true
    override fun isEnabledByDefault() = true
    override fun getShortName() = "DanglingDelete"
    override fun getDisplayName() = "Disallow dangling and conditional deletes"
    override fun getGroupPath() = arrayOf("C/C++", "Static Memory Safety")
    override fun getDefaultLevel(): HighlightDisplayLevel = HighlightDisplayLevel.ERROR
    override fun getStaticDescription() = "A delete that is not followed by a return or function end is a potential memory hazard."

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : OCVisitor() {
            override fun visitMacroCall(macroCall: OCMacroCall?) {
                macroCall?.isSafeCDelete() ?: return

                macroCall.firstParentBefore<OCBlockStatement>()?.findNext<OCReturnStatement>() ?: run {
                    holder.registerProblem(macroCall, "Dangling or conditional delete", DeleteQuickFix())
                }
            }
        }
    }
}
