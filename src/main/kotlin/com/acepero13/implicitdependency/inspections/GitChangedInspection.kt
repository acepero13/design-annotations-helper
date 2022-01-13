package com.acepero13.implicitdependency.inspections

import com.acepero13.implicitdependency.commons.AnnotationUtils
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.vcs.FileStatus
import com.intellij.openapi.vcs.changes.ChangeListManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiReferenceExpression


class GitChangedInspection : AbstractBaseJavaLocalInspectionTool() {


    /**
     * This method is overridden to provide a custom visitor.
     * that inspects expressions with relational operators '==' and '!='.
     * The visitor must not be recursive and must be thread-safe.
     *
     * @param holder     object for visitor to register problems found.
     * @param isOnTheFly true if inspection was run in non-batch mode
     * @return non-null visitor for this inspection.
     * @see JavaElementVisitor
     */
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {


            /**
             * Avoid defining visitors for both Reference and Binary expressions.
             *
             * @param psiReferenceExpression The expression to be evaluated.
             */
            override fun visitReferenceExpression(psiReferenceExpression: PsiReferenceExpression?) {}

            override fun visitAnnotation(annotation: PsiAnnotation?) {
                if (AnnotationUtils.isAnnotatedAsImplicit(annotation?.qualifiedName)) {

                    annotation?.attributes?.asSequence()?.map { annotation.findAttributeValue(it.attributeName) }
                        ?.map { AnnotationContainer.of(it) }?.map { it.listDependencies() }?.flatten()?.filter {
                            ChangeListManager.getInstance(annotation.project).getStatus(it) != FileStatus.NOT_CHANGED
                        }?.forEach { holder.registerProblem(annotation, buildProblemMessage(it)) }


                }
            }


        }
    }

    fun buildProblemMessage(virtualFile: VirtualFile): String {
        return "File ${virtualFile.name} has changed. This file should also change since they have an implicit dependency"
    }
}