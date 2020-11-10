package com.github.wulkanat.safec.extensions

import com.jetbrains.cidr.lang.psi.OCMacroCall

fun OCMacroCall.isSafeCDelete(): String? {
    return if (macroReferenceElement?.text == "delete") arguments.firstOrNull()?.text else null
}
