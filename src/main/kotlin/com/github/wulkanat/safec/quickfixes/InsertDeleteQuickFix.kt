package com.github.wulkanat.safec.quickfixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.jetbrains.cidr.lang.refactoring.util.OCChangeUtil
import com.jetbrains.cidr.lang.util.OCElementFactory

class InsertDeleteQuickFix(private val variableName: String) : LocalQuickFix {
    override fun getFamilyName() = "Insert \"delete($variableName)\""

    override fun applyFix(project: Project, problem: ProblemDescriptor) {
        OCChangeUtil.addBefore(
            problem.startElement.parent,
            OCElementFactory.statementFromText("delete($variableName)", problem.startElement),
            problem.startElement,
        )
    }
}
