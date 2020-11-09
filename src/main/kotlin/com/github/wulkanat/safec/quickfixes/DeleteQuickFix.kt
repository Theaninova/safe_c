package com.github.wulkanat.safec.quickfixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project

class DeleteQuickFix : LocalQuickFix {
    override fun getFamilyName() = "Delete @DirtiesContext"

    override fun applyFix(project: Project, problem: ProblemDescriptor) {
        problem.psiElement.delete()
    }
}