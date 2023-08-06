package com.longcb.vimonintellij

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

class ProjectOpenStartupActivity : StartupActivity.DumbAware {
    override fun runActivity(project: Project) {
        val vimOnIntellijService = ApplicationManager.getApplication().getService(VimOnIntellijService::class.java)

        ApplicationManager.getApplication().invokeLater {
            vimOnIntellijService.getVimInfo()
        }
    }
}
