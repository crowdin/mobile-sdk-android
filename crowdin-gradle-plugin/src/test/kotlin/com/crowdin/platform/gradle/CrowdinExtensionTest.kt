package com.crowdin.platform.gradle

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CrowdinExtensionTest {

    @Test
    fun `default values are correct`() {
        val extension = CrowdinExtension()
        assertTrue("Default enableInDebug should be true", extension.enableInDebug)
        assertFalse("Default enableInRelease should be false", extension.enableInRelease)
    }
}
