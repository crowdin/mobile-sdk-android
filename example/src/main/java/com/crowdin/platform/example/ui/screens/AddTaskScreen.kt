package com.crowdin.platform.example.ui.screens

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.crowdin.platform.example.R
import com.crowdin.platform.example.category.DBManagerCategory
import com.crowdin.platform.example.task.DBManagerTask
import com.crowdin.platform.example.ui.components.AddCategoryDialog
import com.crowdin.platform.example.ui.localizedString
import com.crowdin.platform.example.utils.getFormatDate
import com.crowdin.platform.example.utils.getFormatTime
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

private const val STORED_DATE_PATTERN = "yyyy-MM-dd"

/**
 * Full screen form for a new task. The fields are the ones the database already stores:
 * title, task, category, date and time.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddTaskScreen(
    onClose: () -> Unit,
    onSaved: () -> Unit,
) {
    val context = LocalContext.current
    val taskManager = remember { DBManagerTask(context) }
    val categoryManager = remember { DBManagerCategory(context) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var task by remember { mutableStateOf("") }
    var storedDate by remember { mutableStateOf("") }
    var storedTime by remember { mutableStateOf("") }
    var categories by remember { mutableStateOf(categoryManager.getListOfCategory()) }
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull().orEmpty()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }

    fun reloadCategories() {
        categories = categoryManager.getListOfCategory()
        if (selectedCategory !in categories) {
            selectedCategory = categories.firstOrNull().orEmpty()
        }
    }

    val emptyTitleMessage = localizedString(R.string.please_add_title)
    val emptyTaskMessage = localizedString(R.string.please_add_task)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(localizedString(R.string.add_task)) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            painter = painterResource(R.drawable.ic_close_black_24dp),
                            contentDescription = localizedString(R.string.cancel),
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.surfaceContainerLow) {
                Button(
                    onClick = {
                        val taskTitle = title.trim()
                        val taskText = task.trim()
                        when {
                            taskTitle.isEmpty() ->
                                scope.launch { snackbarHostState.showSnackbar(emptyTitleMessage) }

                            taskText.isEmpty() ->
                                scope.launch { snackbarHostState.showSnackbar(emptyTaskMessage) }

                            else -> {
                                taskManager.insert(
                                    taskTitle,
                                    taskText,
                                    selectedCategory,
                                    storedDate,
                                    storedTime,
                                )
                                onSaved()
                            }
                        }
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_check_black_24dp),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = localizedString(R.string.add_task),
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(localizedString(R.string.task_title)) },
                placeholder = { Text(localizedString(R.string.enter_task_title)) },
                singleLine = true,
                keyboardOptions =
                    KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next,
                    ),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            )

            OutlinedTextField(
                value = task,
                onValueChange = { task = it },
                label = { Text(localizedString(R.string.what_is_to_be_done)) },
                placeholder = { Text(localizedString(R.string.enter_your_task)) },
                minLines = 3,
                keyboardOptions =
                    KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                text = localizedString(R.string.set_date_and_time),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            PickerField(
                value = if (storedDate.isEmpty()) "" else getFormatDate(storedDate),
                labelRes = R.string.set_date,
                iconRes = R.drawable.ic_date_range_black_24dp,
                onClick = { showDatePicker = true },
                onClear = {
                    storedDate = ""
                    storedTime = ""
                },
                modifier = Modifier.fillMaxWidth(),
            )

            if (storedDate.isNotEmpty()) {
                PickerField(
                    value = if (storedTime.isEmpty()) "" else getFormatTime(storedTime),
                    labelRes = R.string.set_time,
                    iconRes = R.drawable.ic_access_time_black_24dp,
                    onClick = { showTimePicker = true },
                    onClear = { storedTime = "" },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Text(
                text = localizedString(R.string.add_to_category),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (categories.isEmpty()) {
                Text(
                    text = localizedString(R.string.no_category_to_added),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            ) {
                categories.forEach { category ->
                    FilterChip(
                        selected = category == selectedCategory,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                    )
                }
                AssistChip(
                    onClick = { showAddCategoryDialog = true },
                    label = { Text(localizedString(R.string.add_category)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_playlist_add_black_24dp),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                    },
                )
            }
        }
    }

    if (showDatePicker) {
        TaskDatePicker(
            initialDate = storedDate,
            onDismiss = { showDatePicker = false },
            onSelected = {
                storedDate = it
                showDatePicker = false
            },
        )
    }

    if (showTimePicker) {
        TaskTimePicker(
            initialTime = storedTime,
            onDismiss = { showTimePicker = false },
            onSelected = {
                storedTime = it
                showTimePicker = false
            },
        )
    }

    if (showAddCategoryDialog) {
        AddCategoryDialog(
            onDismiss = { showAddCategoryDialog = false },
            onConfirm = { name ->
                categoryManager.insert(name)
                showAddCategoryDialog = false
                reloadCategories()
                selectedCategory = name
            },
        )
    }
}

/**
 * Read-only text field that opens a picker when tapped.
 */
@Composable
private fun PickerField(
    value: String,
    labelRes: Int,
    iconRes: Int,
    onClick: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) onClick()
        }
    }

    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = { Text(localizedString(labelRes)) },
        trailingIcon = {
            if (value.isEmpty()) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                )
            } else {
                IconButton(onClick = onClear) {
                    Icon(
                        painter = painterResource(R.drawable.ic_cancel_black_24dp),
                        contentDescription = localizedString(R.string.cancel),
                    )
                }
            }
        },
        interactionSource = interactionSource,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskDatePicker(
    initialDate: String,
    onDismiss: () -> Unit,
    onSelected: (String) -> Unit,
) {
    val todayMillis =
        remember {
            Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }
    val storedFormat =
        remember {
            SimpleDateFormat(STORED_DATE_PATTERN, Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
        }
    val state =
        rememberDatePickerState(
            initialSelectedDateMillis =
                initialDate
                    .takeIf { it.isNotEmpty() }
                    ?.let { runCatching { storedFormat.parse(it)?.time }.getOrNull() }
                    ?: todayMillis,
            selectableDates =
                object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                        utcTimeMillis >= todayMillis
                },
        )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    state.selectedDateMillis?.let { onSelected(storedFormat.format(it)) }
                        ?: onDismiss()
                },
            ) {
                Text(stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(android.R.string.cancel)) }
        },
    ) {
        DatePicker(state = state)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskTimePicker(
    initialTime: String,
    onDismiss: () -> Unit,
    onSelected: (String) -> Unit,
) {
    val now = remember { Calendar.getInstance() }
    val initialParts = initialTime.split(":").mapNotNull { it.toIntOrNull() }
    val state =
        rememberTimePickerState(
            initialHour = initialParts.getOrNull(0) ?: now.get(Calendar.HOUR_OF_DAY),
            initialMinute = initialParts.getOrNull(1) ?: now.get(Calendar.MINUTE),
            is24Hour = false,
        )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onSelected(
                        String.format(
                            Locale.US,
                            "%02d:%02d",
                            state.hour,
                            state.minute,
                        ),
                    )
                },
            ) {
                Text(stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(android.R.string.cancel)) }
        },
        text = { TimePicker(state = state) },
    )
}
