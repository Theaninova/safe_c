package com.github.wulkanat.safec

import com.github.wulkanat.safec.extensions.checkBox
import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ex.ProblemDescriptorImpl
import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.ui.VerticalFlowLayout
import com.intellij.psi.PsiFile
import com.jetbrains.cidr.lang.inspections.OCInspections
import com.jetbrains.cidr.lang.psi.OCDeclaration
import javax.swing.JComponent
import javax.swing.JPanel

class CMem : OCInspections.GeneralCpp() {
    override fun worksWithClangd() = true
    override fun isEnabledByDefault() = true
    override fun getShortName() = "CMem"
    override fun getDisplayName() = "Disallow top-level borrows"
    override fun getGroupPath() = arrayOf("C/C++", "Static Memory Safety")
    override fun getDefaultLevel(): HighlightDisplayLevel = HighlightDisplayLevel.ERROR
    override fun getStaticDescription() = "Static Memory analysis for compatible code"

    var disallowTopLevelBorrows = true

    override fun createOptionsPanel(): JComponent? {
        return JPanel(VerticalFlowLayout()).apply {
            add(checkBox("Disallow top-level borrows") { disallowTopLevelBorrows = it })
        }
    }

    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor?>? {
        /* if (!file.isCFile())*/ return ProblemDescriptor.EMPTY_ARRAY

        // file.children.filterIsInstance<OCFunctionDeclaration>()
        // PluginManager.getLogger().info("SafeC Works!")

        // return disallowTopLevelBorrows(file)
    }


    private fun disallowTopLevelBorrows(file: PsiFile): Array<ProblemDescriptor?>? {
        val problems = mutableListOf<ProblemDescriptor>()

        file.children.filterIsInstance<OCDeclaration>().forEach {
            if (it.typeElement?.text?.matches(VariableOwnershipStatus.BORROWED.pattern) == true) {
                problems.add(ProblemDescriptorImpl(
                        it, it,
                        "No top level borrows",
                        arrayOf(),
                        ProblemHighlightType.ERROR,
                        true,
                        null, false,
                ))
            }
        }

        return problems.toTypedArray()
    }
}