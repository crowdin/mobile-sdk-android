package com.crowdin.platform.example.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.crowdin.platform.example.R
import com.crowdin.platform.example.task.DBManagerTask
import com.crowdin.platform.example.task.model.TaskModel
import com.crowdin.platform.example.ui.components.DestinationScaffold
import com.crowdin.platform.example.ui.components.EmptyState
import com.crowdin.platform.example.ui.components.TaskList

/**
 * Completed tasks. Swipe right puts a task back on the list, swipe left removes it for good.
 */
@Composable
fun HistoryScreen(bottomBar: @Composable () -> Unit) {
    val context = LocalContext.current
    val dbManager = remember { DBManagerTask(context) }
    var tasks by remember { mutableStateOf(dbManager.getHistoryTaskList() as List<TaskModel>) }

    DestinationScaffold(
        titleRes = R.string.history,
        bottomBar = bottomBar,
    ) { padding ->
        if (tasks.isEmpty()) {
            EmptyState(
                iconRes = R.drawable.ic_history_black_24dp,
                titleRes = R.string.no_history_to_show,
                descriptionRes = R.string.empty_history_description,
                modifier = Modifier.padding(padding),
            )
        } else {
            TaskList(
                tasks = tasks,
                completed = true,
                screenPadding = padding,
                onToggleCompleted = { task ->
                    dbManager.unFinishTask(task.id)
                    tasks = dbManager.getHistoryTaskList()
                },
                onDelete = { task ->
                    dbManager.delete(task.id)
                    tasks = dbManager.getHistoryTaskList()
                },
            )
        }
    }
}
