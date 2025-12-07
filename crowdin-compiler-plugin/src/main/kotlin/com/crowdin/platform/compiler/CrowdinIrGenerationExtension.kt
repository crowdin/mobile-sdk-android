package com.crowdin.platform.compiler

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid

/**
 * IR generation extension that applies the Crowdin string resource transformer.
 *
 * This extension is invoked during the IR generation phase and applies our
 * transformer to all IR elements in the module.
 */
class CrowdinIrGenerationExtension(
    private val messageCollector: MessageCollector,
    private val enabled: Boolean
) : IrGenerationExtension {

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        if (!enabled) {
            messageCollector.report(CompilerMessageSeverity.LOGGING, "[Crowdin IrGenerationExtension] Plugin disabled, skipping transformation")
            return
        }

        // Check if Compose has already run
        if (hasComposeTransformations(moduleFragment)) {
            error(
                """
                ERROR: Compose has already transformed the IR!
                CrowdinGradlePlugin must run BEFORE Compose.

                Make sure CrowdinGradlePlugin is applied before Compose:
                plugins {
                    id("com.crowdin.platform.gradle")
                    id("org.jetbrains.kotlin.plugin.compose")
                }
                """.trimIndent()
            )
        }

        messageCollector.report(CompilerMessageSeverity.LOGGING, "[Crowdin IrGenerationExtension] Applying transformer to module: ${moduleFragment.name}")

        // Apply the transformer to all IR elements in the module
        val transformer = CrowdinStringResourceTransformer(pluginContext, enabled)
        moduleFragment.transformChildrenVoid(transformer)
    }

    private fun hasComposeTransformations(moduleFragment: IrModuleFragment): Boolean {
        // Check for Compose-specific IR patterns
        return moduleFragment.files.any { file ->
            file.declarations.any { declaration ->
                when (declaration) {
                    is IrFunction -> {
                        // Compose adds a Composer parameter
                        declaration.parameters.any { param ->
                            param.type.classFqName?.asString() ==
                                "androidx.compose.runtime.Composer"
                        }
                    }
                    else -> false
                }
            }
        }
    }
}
