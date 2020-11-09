package com.github.wulkanat.safec.quickfixes

import com.intellij.codeInspection.LocalQuickFixOnPsiElement
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

class ReplaceExpressionQuickFix(replace: PsiElement, with: PsiElement) : LocalQuickFixOnPsiElement(replace, with) {
    override fun getFamilyName() = "Replace with \"${endElement.text}\""
    override fun getText() = "Replace with \"${endElement.text}\""

    override fun invoke(project: Project, file: PsiFile, replace: PsiElement, with: PsiElement) {
        replace.replace(with)
    }
}