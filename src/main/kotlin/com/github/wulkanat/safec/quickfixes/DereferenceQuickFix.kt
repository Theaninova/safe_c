package com.github.wulkanat.safec.quickfixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.jetbrains.cidr.lang.util.OCElementFactory

class DereferenceQuickFix : LocalQuickFix {
    override fun getFamilyName() = "Dereference variable"

    override fun applyFix(project: Project, problem: ProblemDescriptor) {
        OCElementFactory.expressionFromText("*(${problem.startElement.text})", problem.startElement)?.let {
            problem.startElement.replace(it)
        }
    }
}
