package com.crowdin.platform.example.ui

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.crowdin.platform.example.R
import com.crowdin.platform.example.ui.screens.AddTaskScreen
import com.crowdin.platform.example.ui.screens.CategoriesScreen
import com.crowdin.platform.example.ui.screens.HistoryScreen
import com.crowdin.platform.example.ui.screens.SettingsScreen
import com.crowdin.platform.example.ui.screens.TasksScreen

/**
 * Top level destinations.
 */
private enum class Destination(
    @param:StringRes val labelRes: Int,
    @param:DrawableRes val iconRes: Int,
) {
    TASKS(R.string.dashboard, R.drawable.ic_dashboard_black_24dp),
    CATEGORIES(R.string.category, R.drawable.ic_assignment_black_24dp),
    HISTORY(R.string.history, R.drawable.ic_history_black_24dp),
    SETTINGS(R.string.settings, R.drawable.ic_settings_black_24dp),
}

@Composable
fun CrowdinTodoApp() {
    var destination by rememberSaveable { mutableStateOf(Destination.TASKS) }
    var addingTask by rememberSaveable { mutableStateOf(false) }
    var tasksRevision by rememberSaveable { mutableIntStateOf(0) }

    if (addingTask) {
        BackHandler { addingTask = false }
        AddTaskScreen(
            onClose = { addingTask = false },
            onSaved = {
                tasksRevision++
                addingTask = false
            },
        )
        return
    }

    val bottomBar: @Composable () -> Unit = {
        BoxWithConstraints {
            val labelWidth = maxWidth / Destination.entries.size - 8.dp
            NavigationBar {
                Destination.entries.forEach { item ->
                    NavigationBarItem(
                        selected = item == destination,
                        onClick = { destination = item },
                        icon = {
                            Icon(
                                painter = painterResource(item.iconRes),
                                contentDescription = null,
                            )
                        },
                        label = {
                            Text(
                                text = localizedString(item.labelRes),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(labelWidth),
                            )
                        },
                    )
                }
            }
        }
    }

    when (destination) {
        Destination.TASKS ->
            key(tasksRevision) {
                TasksScreen(
                    onAddTask = { addingTask = true },
                    bottomBar = bottomBar,
                )
            }

        Destination.CATEGORIES -> CategoriesScreen(bottomBar = bottomBar)
        Destination.HISTORY -> HistoryScreen(bottomBar = bottomBar)
        Destination.SETTINGS -> SettingsScreen(bottomBar = bottomBar)
    }
}
