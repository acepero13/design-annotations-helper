package com.acepero13.implicitdependency.inspections

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool
import com.intellij.codeInspection.InspectionsBundle
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.vcs.FileStatus
import com.intellij.openapi.vcs.changes.ChangeListManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*


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
             * This string defines the short message shown to a user signaling the inspection found a problem.
             * It reuses a string from the inspections bundle.
             */
            private val DESCRIPTION_TEMPLATE = "SDK " +
                    InspectionsBundle.message("inspection.comparing.references.problem.descriptor")

            /**
             * Avoid defining visitors for both Reference and Binary expressions.
             *
             * @param psiReferenceExpression The expression to be evaluated.
             */
            override fun visitReferenceExpression(psiReferenceExpression: PsiReferenceExpression?) {}

            override fun visitAnnotation(annotation: PsiAnnotation?) {
                if (annotation?.qualifiedName.equals("ImplicitDependency")) {

                    annotation?.attributes?.asSequence()?.map {
                        annotation.findAttributeValue(it.attributeName)
                    }
                        ?.map { AnnotationContainer.of(it) }
                        ?.map { it.listDependencies() }
                        ?.flatten()
                        ?.filter { it -> ChangeListManager.getInstance(annotation.project).getStatus(it) != FileStatus.NOT_CHANGED }
                        ?.forEach { it ->  holder.registerProblem(annotation, buildProblemMessage(it))}


                }
            }


        }
    }

    fun buildProblemMessage(virtualFile: VirtualFile):String {
        return "File ${virtualFile.name} has changed. This file should also change since they have an implicit dependency"
    }
}