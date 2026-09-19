package com.crowdin.platform.example.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
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
import com.crowdin.platform.example.task.DBManagerTask
import com.crowdin.platform.example.task.model.TaskModel
import com.crowdin.platform.example.ui.components.DestinationScaffold
import com.crowdin.platform.example.ui.components.EmptyState
import com.crowdin.platform.example.ui.components.TaskList
import com.crowdin.platform.example.ui.localizedString

/**
 * Open tasks. Swipe right completes a task, swipe left removes.
 */
@Composable
fun TasksScreen(
    onAddTask: () -> Unit,
    bottomBar: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val dbManager = remember { DBManagerTask(context) }
    // Read while composing, not in a LaunchedEffect: the database read is synchronous, and
    // deferring it would render one frame of the empty state before the tasks arrive - a visible
    // flash every time this destination is opened.
    var tasks by remember { mutableStateOf(dbManager.getTaskList() as List<TaskModel>) }

    DestinationScaffold(
        titleRes = R.string.dashboard,
        bottomBar = bottomBar,
        floatingActionButton = {
            // The empty state carries its own call to action, so the FAB would only repeat it.
            if (tasks.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = onAddTask,
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_add_black_24dp),
                            contentDescription = null,
                        )
                    },
                    text = { Text(localizedString(R.string.add_task)) },
                )
            }
        },
    ) { padding ->
        if (tasks.isEmpty()) {
            EmptyState(
                iconRes = R.drawable.ic_check_black_24dp,
                titleRes = R.string.no_task_to_show,
                descriptionRes = R.string.empty_tasks_description,
                modifier = Modifier.padding(padding),
                actionRes = R.string.add_task,
                onAction = onAddTask,
            )
        } else {
            TaskList(
                tasks = tasks,
                completed = false,
                screenPadding = padding,
                onToggleCompleted = { task ->
                    dbManager.finishTask(task.id)
                    tasks = dbManager.getTaskList()
                },
                onDelete = { task ->
                    dbManager.delete(task.id)
                    tasks = dbManager.getTaskList()
                },
                // Clears the extended floating action button.
                bottomSpacing = 96.dp,
            )
        }
    }
}
