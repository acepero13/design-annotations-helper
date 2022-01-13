package com.acepero13.implicitdependency.references

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.util.ProcessingContext
import org.jetbrains.annotations.NotNull

class FileReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(@NotNull psiReferenceRegistrar: PsiReferenceRegistrar) {
        psiReferenceRegistrar.registerReferenceProvider(
            PlatformPatterns.psiElement(),
            psiReferenceProvider()
        )
    }

    private fun psiReferenceProvider() = object : PsiReferenceProvider() {
        override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
            if (element is PsiLiteralExpression && belongsToAnnotation(element)) {
                return getFileReferences(element).toTypedArray()
            }
            return arrayOf()
        }


        private fun belongsToAnnotation(element: PsiLiteralExpression): Boolean {
            var parent = element.parent
            while (parent != null) {
                if (parent is PsiAnnotation && parent.qualifiedName.equals("ImplicitDependency")) {
                    return true
                }
                parent = parent.parent
            }
            return false
        }
    }


}

val filePathPattern = Regex("[A-Za-z0-9.\\-_/]+\\.[A-z0-9]{1,10}")
fun getFileReferences(element: PsiLiteralExpression): List<FilePathReference> {
    return filePathPattern.findAll(element.value as String)
        .map { FilePathReference(element, it.value) }
        .toList()
}