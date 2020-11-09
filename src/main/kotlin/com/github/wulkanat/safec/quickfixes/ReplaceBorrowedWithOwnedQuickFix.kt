package com.github.wulkanat.safec.quickfixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.jetbrains.cidr.lang.psi.OCDeclaration
import com.jetbrains.cidr.lang.util.OCElementFactory

class ReplaceBorrowedWithOwnedQuickFix : LocalQuickFix {
    override fun getFamilyName() = "Use owned variable instead"

    override fun applyFix(project: Project, problem: ProblemDescriptor) {
        val declaration = if (problem.psiElement !is OCDeclaration) return else problem.psiElement as OCDeclaration
        declaration.replace(OCElementFactory.declarationFromText(
                declaration.text.replaceFirst("borrowed", "owned"),
                declaration,
        ))
    }
}