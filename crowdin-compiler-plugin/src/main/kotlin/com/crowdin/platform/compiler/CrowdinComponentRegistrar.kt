package com.crowdin.platform.compiler

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

/**
 * Component registrar for the Crowdin compiler plugin.
 *
 * This is the entry point for the Kotlin compiler to discover and initialize
 * our plugin. It registers the IR generation extension that performs the
 * stringResource -> crowdinString transformation.
 */
class CrowdinComponentRegistrar : CompilerPluginRegistrar() {

    companion object {
        val KEY_ENABLED = CompilerConfigurationKey<String>("crowdin.enabled")
        const val PLUGIN_ID = "com.crowdin.platform.compiler"
    }

    override val supportsK2: Boolean = true  // Now using K2-compatible APIs

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val messageCollector = configuration.get(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        // Check if the plugin is enabled (defaults to false for release builds)
        val enabled = configuration.get(KEY_ENABLED, "false").toBoolean()

        // Register our IR generation extension
        IrGenerationExtension.registerExtension(CrowdinIrGenerationExtension(messageCollector, enabled))
    }
}
