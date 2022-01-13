package com.acepero13.implicitdependency.commons

object AnnotationUtils {
    fun isAnnotatedAsImplicit(name: String?): Boolean {
        return name?.contains("ImplicitDependency") == true
    }
}