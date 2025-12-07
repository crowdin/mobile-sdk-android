package com.crowdin.platform.compiler

import com.crowdin.platform.compiler.runners.AbstractJvmBoxTest
import org.jetbrains.kotlin.generators.generateTestGroupSuiteWithJUnit5

fun main() {
    generateTestGroupSuiteWithJUnit5 {
        testGroup(testDataRoot = "crowdin-compiler-plugin/testData", testsRoot = "crowdin-compiler-plugin/test-gen") {
            testClass<AbstractJvmBoxTest> {
                model("box")
            }
        }
    }
}