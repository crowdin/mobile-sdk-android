package com.crowdin.platform.gradle

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

/**
 * Gradle plugin that applies the Crowdin compiler plugin to intercept stringResource calls.
 *
 * This plugin automatically configures the Kotlin compiler plugin based on the build variant,
 * enabling transparent stringResource interception in debug builds while keeping release builds clean.
 *
 * Usage in app's build.gradle:
 * ```
 * plugins {
 *     id 'com.crowdin.platform.gradle'
 * }
 *
 * crowdin {
 *     enableInDebug = true  // Default
 *     enableInRelease = false  // Default
 * }
 * ```
 */
class CrowdinGradlePlugin : KotlinCompilerPluginSupportPlugin {

    override fun apply(target: Project) {
        // Check if Compose is already applied
        if (target.plugins.hasPlugin("org.jetbrains.kotlin.plugin.compose")) {
            throw GradleException(
                """
                ERROR: The Crowdin plugin (com.crowdin.platform.gradle) must be applied BEFORE the Compose plugin.
                
                Correct order in build.gradle.kts:
                plugins {
                    id("com.crowdin.platform.gradle")                    // First
                    id("org.jetbrains.kotlin.plugin.compose")            // Second
                }
                """.trimIndent()
            )
        }

        super.apply(target)

        target.extensions.create("crowdin", CrowdinExtension::class.java)
    }


    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
        // The plugin is applicable to all Kotlin compilations
        return true
    }

    override fun getCompilerPluginId(): String {
        val pluginId = "com.crowdin.platform.compiler"
        return pluginId
    }

    override fun getPluginArtifact(): SubpluginArtifact {
        val artifact = SubpluginArtifact(
            groupId = BuildConfig.PLUGIN_GROUP_ID,
            artifactId = "compiler-plugin",
            version = BuildConfig.CROWDIN_VERSION
        )
        return artifact
    }

    override fun applyToCompilation(
        kotlinCompilation: KotlinCompilation<*>
    ): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project

        return project.provider {
            val extension = project.extensions.findByType(CrowdinExtension::class.java)
                ?: CrowdinExtension()

            // Determine if we should enable the plugin based on the compilation name
            val isDebugVariant = kotlinCompilation.name.contains("debug", ignoreCase = true)
            val isReleaseVariant = kotlinCompilation.name.contains("release", ignoreCase = true)

            val enabled = when {
                isDebugVariant -> extension.enableInDebug
                isReleaseVariant -> extension.enableInRelease
                else -> false  // Unknown variant, default to disabled
            }

            project.logger.debug("[Crowdin Gradle Plugin] applyToCompilation() returning SubpluginOption(enabled=$enabled)")

            listOf(
                SubpluginOption(key = "enabled", value = enabled.toString())
            )
        }
    }
}

/**
 * Extension for configuring the Crowdin Gradle plugin.
 */
open class CrowdinExtension {
    /**
     * Enable the plugin in debug builds.
     * Default: true
     */
    var enableInDebug: Boolean = true

    /**
     * Enable the plugin in release builds.
     * Default: false (recommended to keep release builds clean and fast)
     */
    var enableInRelease: Boolean = false
}
