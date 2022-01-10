package com.acepero13.implicitdependency.references

import com.intellij.openapi.externalSystem.model.ProjectKeys.CONTENT_ROOT
import com.intellij.psi.PsiLiteralExpression
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import junit.framework.TestCase
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.swing.SwingUtilities

class FileReferenceContributorTest: LightJavaCodeInsightFixtureTestCase() {


    @BeforeEach
    fun init() {
        super.setUp()
        myFixture.configureByFile("Annotation.java")
        myFixture.allowTreeAccessForAllFiles()
    }


    @Test
    fun testGetFileReferences() {
        doTest {

            val fileDependency = myFixture.file.findElementAt(104)?.parent
            val fileReferences = getFileReferences(fileDependency as PsiLiteralExpression)

            Assertions.assertEquals(1, fileReferences.size)
            TestCase.assertEquals("dependency.json", fileReferences[0].value)
        }
    }

    private fun doTest(run: Runnable) {

        SwingUtilities.invokeAndWait(run)
        //run.run()
    }

    override fun getTestDataPath(): String {
        return "testData"
    }
}