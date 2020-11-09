package com.github.wulkanat.safec

enum class VariableOwnershipStatus(val pattern: Regex) {
    OWNED("\\w+_owned".toRegex()), BORROWED("\\w+_borrowed".toRegex())
}