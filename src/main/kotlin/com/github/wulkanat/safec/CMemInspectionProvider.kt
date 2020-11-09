package com.github.wulkanat.safec

import com.github.wulkanat.safec.inspections.DisallowTopLevelBorrowsInspection
import com.github.wulkanat.safec.inspections.EnforceDeletionWhenOwnedInspection
import com.github.wulkanat.safec.inspections.MovedVariableInspection
import com.github.wulkanat.safec.inspections.UsingRawFieldWithoutDereferenceInspection
import com.intellij.codeInspection.InspectionToolProvider
import com.intellij.codeInspection.LocalInspectionTool

class CMemInspectionProvider : InspectionToolProvider {
    override fun getInspectionClasses(): Array<Class<out LocalInspectionTool>> {
        return arrayOf(
            DisallowTopLevelBorrowsInspection::class.java,
            MovedVariableInspection::class.java,
            EnforceDeletionWhenOwnedInspection::class.java,
            UsingRawFieldWithoutDereferenceInspection::class.java,
        )
    }
}
