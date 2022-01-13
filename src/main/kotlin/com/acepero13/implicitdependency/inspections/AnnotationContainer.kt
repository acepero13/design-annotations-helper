package com.acepero13.implicitdependency.inspections

import com.acepero13.implicitdependency.references.getFileReferences
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*

sealed interface AnnotationContainer {
    companion object {
        fun of(value: PsiClassObjectAccessExpression): AnnotationContainer{
            return DependsOn(value)
        }

        fun of(value: PsiLiteralExpression): AnnotationContainer{
            return SingleFileDependency(value)
        }

        fun of(value: PsiArrayInitializerMemberValue): AnnotationContainer{
            return MultipleFileDependency(value)
        }

        fun of(value: PsiAnnotationMemberValue?): AnnotationContainer {
            when(value) {
                is PsiClassObjectAccessExpression -> return of(value)
                is PsiLiteralExpression -> return of(value)
                is PsiArrayInitializerMemberValue -> return of(value)
            }
            return DummyAnnotation
        }
    }



    fun listDependencies(): List<VirtualFile>

}

object DummyAnnotation : AnnotationContainer {
    override fun listDependencies(): List<VirtualFile> {
       return listOf()
    }

}

class DependsOn(
    private val value: PsiClassObjectAccessExpression) : AnnotationContainer {

    override fun listDependencies(): List<VirtualFile> {
        return listOf(value.containingFile.virtualFile)
    }



}

class SingleFileDependency(private val value: PsiLiteralExpression) : AnnotationContainer {



    override fun listDependencies(): List<VirtualFile> {
        val file = getFileReferences(value)
        if(file.isNotEmpty()) {
            val resolvedFile = file[0].resolve()?.containingFile?.virtualFile
            return if(resolvedFile == null)  listOf() else listOf(resolvedFile)
        }
        return listOf()
    }
}

class MultipleFileDependency(private val value: PsiArrayInitializerMemberValue) : AnnotationContainer {

    override fun listDependencies(): List<VirtualFile>{
        return value.initializers
            .filterIsInstance<PsiLiteralExpression>()
            .flatMap { getFileReferences(it) }
            .map { it.resolve() }
            .map { it?.containingFile }
            .map { it?.virtualFile!! }
    }
}