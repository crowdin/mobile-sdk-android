package com.crowdin.platform.example.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.ImeAction
import com.crowdin.platform.example.R
import com.crowdin.platform.example.ui.localizedString

/**
 * Dialog used to create a category, from both the category list and the new task screen.
 */
@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var errorRes by remember { mutableStateOf<Int?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(localizedString(R.string.add_category)) },
        text = {
            CategoryNameField(
                value = name,
                onValueChange = {
                    name = it
                    errorRes = null
                },
                errorRes = errorRes,
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val categoryName = name.trim()
                    if (categoryName.isEmpty()) {
                        errorRes = R.string.please_enter_category_to_add
                    } else {
                        onConfirm(categoryName)
                    }
                },
            ) {
                Text(localizedString(R.string.add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(localizedString(R.string.cancel)) }
        },
    )
}

/**
 * Dialog used to rename an existing category.
 */
@Composable
fun RenameCategoryDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var name by remember { mutableStateOf(currentName) }
    var errorRes by remember { mutableStateOf<Int?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(localizedString(R.string.update_category)) },
        text = {
            CategoryNameField(
                value = name,
                onValueChange = {
                    name = it
                    errorRes = null
                },
                errorRes = errorRes,
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val categoryName = name.trim()
                    when {
                        categoryName.isEmpty() ->
                            errorRes = R.string.please_enter_something_to_update

                        categoryName == currentName ->
                            errorRes = R.string.please_edit_category_to_update

                        else -> onConfirm(categoryName)
                    }
                },
            ) {
                Text(localizedString(R.string.update))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(localizedString(R.string.cancel)) }
        },
    )
}

/**
 * Confirmation shown before a category is removed.
 */
@Composable
fun DeleteCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(localizedString(R.string.dialog_delete_category_title)) },
        text = { Text(localizedString(R.string.dialog_delete_category_desc)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = localizedString(R.string.delete),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(localizedString(R.string.cancel)) }
        },
    )
}

@Composable
private fun CategoryNameField(
    value: String,
    onValueChange: (String) -> Unit,
    errorRes: Int?,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(localizedString(R.string.category)) },
        placeholder = { Text(localizedString(R.string.enter_category)) },
        singleLine = true,
        isError = errorRes != null,
        supportingText = errorRes?.let { { Text(localizedString(it)) } },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
    )
}
