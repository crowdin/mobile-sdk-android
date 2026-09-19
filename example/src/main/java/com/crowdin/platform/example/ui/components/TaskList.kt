package com.crowdin.platform.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.crowdin.platform.example.task.model.TaskModel

/**
 * The list body shared by the task and history destinations. They differ only in whether their
 * tasks are completed and in how much room the floating action button needs at the bottom.
 */
@Composable
fun TaskList(
    tasks: List<TaskModel>,
    completed: Boolean,
    screenPadding: PaddingValues,
    onToggleCompleted: (TaskModel) -> Unit,
    onDelete: (TaskModel) -> Unit,
    modifier: Modifier = Modifier,
    bottomSpacing: Dp = 16.dp,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding =
            PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = screenPadding.calculateTopPadding() + 8.dp,
                bottom = screenPadding.calculateBottomPadding() + bottomSpacing,
            ),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(items = tasks, key = { it.id }) { task ->
            SwipeableTaskRow(
                task = task,
                completed = completed,
                onToggleCompleted = { onToggleCompleted(task) },
                onDelete = { onDelete(task) },
            )
        }
    }
}
