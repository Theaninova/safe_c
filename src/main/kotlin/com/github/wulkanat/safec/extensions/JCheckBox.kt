package com.github.wulkanat.safec.extensions

import javax.swing.JCheckBox

inline fun checkBox(label: String, defaultValue: Boolean = true, crossinline onChange: (Boolean) -> Unit): JCheckBox {
    return JCheckBox("Disallow top-level borrows", null, defaultValue).apply {
        addItemListener {
            onChange(it.stateChange == 1)
        }
    }
}
