package com.crowdin.platform.compiler

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

/**
 * IR transformer that intercepts Compose resource calls and replaces them with Crowdin-aware
 * composables.
 *
 * This transformer runs during the IR lowering phase and rewrites function calls at the
 * intermediate representation level, making it completely transparent to the user.
 *
 * Uses K2-compatible CallableId API for function resolution.
 */
class CrowdinStringResourceTransformer(
    private val pluginContext: IrPluginContext,
    private val enabled: Boolean
) : IrElementTransformerVoidWithContext() {
    companion object {
        // The FQN of the original stringResource function from Compose
        private val STRING_RESOURCE_FQN = FqName("androidx.compose.ui.res.stringResource")
        private val PLURAL_STRING_RESOURCE_FQN = FqName("androidx.compose.ui.res.pluralStringResource")

        // The FQN of our Crowdin replacement functions
        private val CROWDIN_STRING_NAME = Name.identifier("crowdinString")
        private val CROWDIN_PLURAL_STRING_NAME = Name.identifier("crowdinPluralString")

        // Package names for CallableId
        private val CROWDIN_PACKAGE = FqName("com.crowdin.platform.compose")
    }

    override fun visitCall(expression: IrCall): IrExpression {

        // If the plugin is disabled, don't transform anything
        if (!enabled) {
            return super.visitCall(expression)
        }

        val callee = expression.symbol.owner
        val calleeFqName = callee.fqNameWhenAvailable

        val replacementFunctionName =
            when (calleeFqName) {
                STRING_RESOURCE_FQN -> CROWDIN_STRING_NAME
                PLURAL_STRING_RESOURCE_FQN -> CROWDIN_PLURAL_STRING_NAME
                else -> null
            }

        if (replacementFunctionName != null) {

            val callableId = CallableId(CROWDIN_PACKAGE, replacementFunctionName)
            val crowdinSymbols = pluginContext.referenceFunctions(callableId)

            val crowdinSymbol = findMatchingOverload(
                callee,
                crowdinSymbols.map { it.owner }
            )?.symbol

            if (crowdinSymbol != null) {
                expression.symbol = crowdinSymbol

                return expression
            } else {
                val searchedFqn = callableId.toString()
                val originalSignature = buildString {
                    append(callee.name)
                    append("(")
                    append(callee.parameters.joinToString(", ") { it.type.classFqName?.asString() ?: it.type.toString() })
                    append("): ")
                    append(callee.returnType.classFqName?.asString() ?: callee.returnType.toString())
                }
                val candidateCount = crowdinSymbols.size
                val packageName = CROWDIN_PACKAGE.asString()
                error(
                    "Could not find ${replacementFunctionName.asString()} function.\n" +
                    "Searched for: $searchedFqn\n" +
                    "Original signature: $originalSignature\n" +
                    "Package: $packageName\n" +
                    "Number of candidates found: $candidateCount"
                )
            }
        }

        return super.visitCall(expression)
    }

    private fun findMatchingOverload(
        original: IrSimpleFunction,
        candidates: Collection<IrSimpleFunction>
    ): IrSimpleFunction? {
        return candidates.firstOrNull { candidate ->
            // Must have same parameter count
            if (candidate.parameters.size != original.parameters.size) {
                return@firstOrNull false
            }

            // Must have same return type
            if (!areTypesCompatible(candidate.returnType, original.returnType)) {
                return@firstOrNull false
            }

            // All parameters must match by type
            candidate.parameters.zip(original.parameters).all { (candidateParam, originalParam) ->
               areTypesCompatible(candidateParam.type, originalParam.type)
            }
        }
    }

    private fun areTypesCompatible(candidateType: IrType, originalType: IrType): Boolean {
        // Exact match
        if (candidateType == originalType) return true

        // Compare class FQN - no type system needed!
        return candidateType.classFqName == originalType.classFqName
    }
}
