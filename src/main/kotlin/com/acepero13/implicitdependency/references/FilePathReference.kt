package com.acepero13.implicitdependency.references

import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*

class FilePathReference(element: PsiLiteralExpression, private val filePath: String):  PsiReferenceBase<PsiElement>(element) {
    override fun resolve(): PsiElement? {
        val baseDir = element.containingFile.virtualFile.parent
        val file = baseDir.findFileByRelativePath(filePath)
            ?: findFileByProjectPath()
            ?: return null

        return getPsiFile(file)
    }

    private fun getPsiFile(file: VirtualFile): PsiFile? {
        return PsiManager.getInstance(element.project).findFile(file)
    }



    private fun findFileByProjectPath(): VirtualFile? {
        return element.project.guessProjectDir()?.findFileByRelativePath(filePath)
    }

    override fun getVariants(): Array<Any> {
        return arrayOf()
    }

}
