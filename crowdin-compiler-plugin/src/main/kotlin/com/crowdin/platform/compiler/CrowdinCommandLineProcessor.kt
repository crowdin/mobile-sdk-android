package com.crowdin.platform.compiler

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration

/**
 * Command line processor for the Crowdin compiler plugin.
 *
 * This processes the command-line options passed to the compiler plugin
 * and stores them in the compiler configuration.
 */
class CrowdinCommandLineProcessor : CommandLineProcessor {
    override val pluginId: String = CrowdinComponentRegistrar.PLUGIN_ID

    override val pluginOptions: Collection<AbstractCliOption> = listOf(
        CliOption(
            optionName = "enabled",
            valueDescription = "true|false",
            description = "Enable or disable the Crowdin compiler plugin",
            required = false
        )
    )

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ) {
        when (option.optionName) {
            "enabled" -> configuration.put(CrowdinComponentRegistrar.KEY_ENABLED, value)
        }
    }
}
