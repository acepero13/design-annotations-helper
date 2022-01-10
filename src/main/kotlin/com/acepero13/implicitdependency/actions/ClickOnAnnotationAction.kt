package com.acepero13.implicitdependency.actions

import com.intellij.lang.Language
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import java.util.*
import javax.swing.Icon

import com.intellij.psi.*;

class ClickOnAnnotationAction(icon: Icon?) : AnAction(icon) {
    constructor() : this(null) {
    }
    override fun actionPerformed(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.PSI_FILE)
        val lang: Language = e.getData(CommonDataKeys.PSI_FILE)!!.language
        val languageTag = "+[" + lang.displayName.lowercase(Locale.getDefault()) + "]"

        val editor = e.getRequiredData(CommonDataKeys.EDITOR);


        val selectedElement = file?.findElementAt(editor.caretModel.currentCaret.offset)
        if(selectedElement?.isValid == true && selectedElement.textMatches("ImplicitDependency") ){


        }


    }
}