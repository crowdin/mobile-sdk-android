package com.crowdin.platform.example.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.crowdin.platform.example.R
import com.crowdin.platform.example.category.CategoryModel
import com.crowdin.platform.example.category.DBManagerCategory
import com.crowdin.platform.example.ui.components.AddCategoryDialog
import com.crowdin.platform.example.ui.components.CategoryAvatar
import com.crowdin.platform.example.ui.components.DeleteCategoryDialog
import com.crowdin.platform.example.ui.components.DestinationScaffold
import com.crowdin.platform.example.ui.components.EmptyState
import com.crowdin.platform.example.ui.components.RenameCategoryDialog
import com.crowdin.platform.example.ui.components.SectionCard
import com.crowdin.platform.example.ui.components.transparentListItemColors
import com.crowdin.platform.example.ui.localizedString

/**
 * Category list with add, rename and delete.
 */
@Composable
fun CategoriesScreen(bottomBar: @Composable () -> Unit) {
    val context = LocalContext.current
    val dbManager = remember { DBManagerCategory(context) }
    var categories by remember { mutableStateOf(dbManager.getCategoryList() as List<CategoryModel>) }
    var showAddDialog by remember { mutableStateOf(false) }
    var categoryToRename by remember { mutableStateOf<CategoryModel?>(null) }
    var categoryToDelete by remember { mutableStateOf<CategoryModel?>(null) }

    fun reload() {
        categories = dbManager.getCategoryList()
    }

    DestinationScaffold(
        titleRes = R.string.category,
        bottomBar = bottomBar,
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(
                    painter = painterResource(R.drawable.ic_add_black_24dp),
                    contentDescription = localizedString(R.string.add_category),
                )
            }
        },
    ) { padding ->
        if (categories.isEmpty()) {
            EmptyState(
                iconRes = R.drawable.ic_assignment_black_24dp,
                titleRes = R.string.no_category_to_display,
                descriptionRes = R.string.empty_categories_description,
                modifier = Modifier.padding(padding),
                actionRes = R.string.add_category,
                onAction = { showAddDialog = true },
            )
        } else {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                SectionCard {
                    categories.forEach { category ->
                        CategoryRow(
                            category = category,
                            onRename = { categoryToRename = category },
                            onDelete = { categoryToDelete = category },
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddCategoryDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name ->
                dbManager.insert(name)
                showAddDialog = false
                reload()
            },
        )
    }

    categoryToRename?.let { category ->
        RenameCategoryDialog(
            currentName = category.categoryName,
            onDismiss = { categoryToRename = null },
            onConfirm = { name ->
                dbManager.update(category.id, name)
                categoryToRename = null
                reload()
            },
        )
    }

    categoryToDelete?.let { category ->
        DeleteCategoryDialog(
            onDismiss = { categoryToDelete = null },
            onConfirm = {
                dbManager.delete(category.id)
                categoryToDelete = null
                reload()
            },
        )
    }
}

@Composable
private fun CategoryRow(
    category: CategoryModel,
    onRename: () -> Unit,
    onDelete: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(category.categoryName) },
        leadingContent = { CategoryAvatar(name = category.categoryName) },
        trailingContent = {
            Row {
                IconButton(onClick = onRename) {
                    Icon(
                        painter = painterResource(R.drawable.ic_edit_black_24dp),
                        contentDescription = localizedString(R.string.update_category),
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete_black_24dp),
                        contentDescription = localizedString(R.string.dialog_delete_category_title),
                    )
                }
            }
        },
        colors = transparentListItemColors(),
    )
}
