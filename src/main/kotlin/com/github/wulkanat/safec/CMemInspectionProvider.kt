package com.github.wulkanat.safec

import com.github.wulkanat.safec.inspections.DisallowTopLevelBorrows
import com.intellij.codeInspection.InspectionToolProvider
import com.intellij.codeInspection.LocalInspectionTool

class CMemInspectionProvider : InspectionToolProvider {
    override fun getInspectionClasses(): Array<Class<out LocalInspectionTool>> {
        return arrayOf(
                DisallowTopLevelBorrows::class.java
        )
    }
}