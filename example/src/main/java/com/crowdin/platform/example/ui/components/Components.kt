package com.crowdin.platform.example.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.crowdin.platform.example.R
import com.crowdin.platform.example.task.model.TaskModel
import com.crowdin.platform.example.ui.localizedString
import com.crowdin.platform.example.ui.theme.categoryTone
import com.crowdin.platform.example.utils.getFormatDate
import com.crowdin.platform.example.utils.getFormatTime

/**
 * Full screen placeholder shown when a list has nothing in it.
 */
@Composable
fun EmptyState(
    @DrawableRes iconRes: Int,
    @StringRes titleRes: Int,
    @StringRes descriptionRes: Int,
    modifier: Modifier = Modifier,
    @StringRes actionRes: Int? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(112.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(48.dp),
            )
        }
        Text(
            text = localizedString(titleRes),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 24.dp),
        )
        Text(
            text = localizedString(descriptionRes),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )
        if (actionRes != null && onAction != null) {
            Button(
                onClick = onAction,
                modifier = Modifier.padding(top = 24.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add_black_24dp),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = localizedString(actionRes),
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }
    }
}

/**
 * Small tonal label carrying the category color key.
 */
@Composable
fun CategoryLabel(
    name: String,
    modifier: Modifier = Modifier,
) {
    val tone = categoryTone(name)
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = tone.container,
        contentColor = tone.onContainer,
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
        )
    }
}

/**
 * Round avatar with the first letter of a category name.
 */
@Composable
fun CategoryAvatar(
    name: String,
    modifier: Modifier = Modifier,
) {
    val tone = categoryTone(name)
    Box(
        modifier =
            modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(tone.container),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = name.take(1).uppercase(),
            style = MaterialTheme.typography.titleMedium,
            color = tone.onContainer,
        )
    }
}

/**
 * Task row used on both the task list and the history list.
 */
@Composable
fun TaskCard(
    task: TaskModel,
    completed: Boolean,
    onToggleCompleted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tone = categoryTone(task.category)
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier =
                    Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(tone.accent),
            )
            Row(
                modifier = Modifier.padding(start = 4.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.Top,
            ) {
                CompletionButton(
                    completed = completed,
                    onClick = onToggleCompleted,
                )
                Column(
                    modifier = Modifier.weight(1f).padding(top = 12.dp, bottom = 4.dp),
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        color =
                            if (completed) {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (task.task.isNotEmpty()) {
                        Text(
                            text = task.task,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                    TaskMeta(task = task)
                }
                if (task.category.isNotEmpty()) {
                    CategoryLabel(
                        name = task.category,
                        modifier = Modifier.padding(top = 12.dp, start = 8.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskMeta(task: TaskModel) {
    val date = if (task.date.isEmpty()) "" else getFormatDate(task.date)
    val time = if (task.time.isEmpty()) "" else getFormatTime(task.time)
    if (date.isEmpty() && time.isEmpty()) return

    Row(
        modifier = Modifier.padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (date.isNotEmpty()) {
            MetaItem(iconRes = R.drawable.ic_date_range_black_24dp, text = date)
        }
        if (time.isNotEmpty()) {
            MetaItem(
                iconRes = R.drawable.ic_access_time_black_24dp,
                text = time,
                modifier = Modifier.padding(start = if (date.isEmpty()) 0.dp else 12.dp),
            )
        }
    }
}

@Composable
private fun MetaItem(
    @DrawableRes iconRes: Int,
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}

@Composable
private fun CompletionButton(
    completed: Boolean,
    onClick: () -> Unit,
) {
    val description = localizedString(if (completed) R.string.restore else R.string.action_done)
    IconButton(
        onClick = onClick,
        modifier = Modifier.semantics { contentDescription = description },
    ) {
        if (completed) {
            Box(
                modifier =
                    Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_check_black_24dp),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp),
                )
            }
        } else {
            Box(
                modifier =
                    Modifier
                        .size(22.dp)
                        .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape),
            )
        }
    }
}

/**
 * Background revealed while a row is being swiped away.
 */
@Composable
fun SwipeActionBackground(
    color: Color,
    contentColor: Color,
    @DrawableRes iconRes: Int,
    alignment: Alignment,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(color)
                .padding(horizontal = 24.dp),
        contentAlignment = alignment,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = contentColor,
        )
    }
}

/**
 * Task row with the swipe gestures the app has always had:
 * swipe right toggles completion, swipe left deletes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableTaskRow(
    task: TaskModel,
    completed: Boolean,
    onToggleCompleted: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        when (dismissState.currentValue) {
            SwipeToDismissBoxValue.StartToEnd -> onToggleCompleted()
            SwipeToDismissBoxValue.EndToStart -> onDelete()
            SwipeToDismissBoxValue.Settled -> Unit
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        backgroundContent = {
            when (dismissState.dismissDirection) {
                SwipeToDismissBoxValue.StartToEnd ->
                    SwipeActionBackground(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        iconRes =
                            if (completed) {
                                R.drawable.ic_history_black_24dp
                            } else {
                                R.drawable.ic_check_black_24dp
                            },
                        alignment = Alignment.CenterStart,
                    )

                SwipeToDismissBoxValue.EndToStart ->
                    SwipeActionBackground(
                        color = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        iconRes = R.drawable.ic_delete_black_24dp,
                        alignment = Alignment.CenterEnd,
                    )

                SwipeToDismissBoxValue.Settled -> Unit
            }
        },
    ) {
        TaskCard(
            task = task,
            completed = completed,
            onToggleCompleted = onToggleCompleted,
        )
    }
}
