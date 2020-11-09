package com.github.wulkanat.safec

enum class VariableOwnershipStatus(val pattern: Regex) {
    OWNED("\\w_owned".toRegex()), BORROWED("\\w_borrowed".toRegex())
}