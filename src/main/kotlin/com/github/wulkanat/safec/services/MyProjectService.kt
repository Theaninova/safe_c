package com.github.wulkanat.safec.services

import com.intellij.openapi.project.Project
import com.github.wulkanat.safec.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
