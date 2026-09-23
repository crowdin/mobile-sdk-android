package com.crowdin.platform.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import com.crowdin.platform.Crowdin
import com.crowdin.platform.example.App
import com.crowdin.platform.example.BuildConfig
import com.crowdin.platform.example.R
import com.crowdin.platform.example.ui.components.DestinationScaffold
import com.crowdin.platform.example.ui.components.SectionCard
import com.crowdin.platform.example.ui.components.clickableRow
import com.crowdin.platform.example.ui.components.transparentListItemColors
import com.crowdin.platform.example.ui.localizedString
import java.util.Locale
import androidx.core.net.toUri

private const val REPOSITORY_URL = "https://github.com/crowdin/mobile-sdk-android"

/**
 * Settings: the app language picker and a link to the SDK repository.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(bottomBar: @Composable () -> Unit) {
    val context = LocalContext.current
    var showLanguageSheet by remember { mutableStateOf(false) }
    var currentLocale by remember { mutableStateOf(currentAppLocale()) }

    DestinationScaffold(
        titleRes = R.string.settings,
        bottomBar = bottomBar,
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
        ) {
            SectionCard {
                ListItem(
                    headlineContent = { Text(localizedString(R.string.app_language)) },
                    supportingContent = { Text(languageLabel(currentLocale)) },
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.ic_language_black_24dp),
                            contentDescription = null,
                        )
                    },
                    colors = transparentListItemColors(),
                    modifier = Modifier.clickableRow { showLanguageSheet = true },
                )
            }

            SectionCard {
                ListItem(
                    headlineContent = { Text(localizedString(R.string.nav_header_title)) },
                    supportingContent = {
                        Text(
                            text = localizedString(R.string.nav_header_sub_title),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.ic_open_in_new_black_24dp),
                            contentDescription = null,
                        )
                    },
                    colors = transparentListItemColors(),
                    modifier = Modifier.clickableRow { openRepository(context) },
                )
            }

            Spacer(modifier = Modifier.navigationBarsPadding().padding(bottom = 16.dp))
        }
    }

    if (showLanguageSheet) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { showLanguageSheet = false },
            sheetState = sheetState,
        ) {
            LanguageSheetContent(
                selected = currentLocale,
                onSelect = { locale ->
                    showLanguageSheet = false
                    if (locale != currentLocale) {
                        applyLocale(context, locale)
                        currentLocale = locale
                    }
                },
            )
        }
    }
}

@Composable
private fun LanguageSheetContent(
    selected: String,
    onSelect: (String) -> Unit,
) {
    val context = LocalContext.current
    val languages = remember { languageOptions(context) }

    Column(modifier = Modifier.fillMaxWidth().navigationBarsPadding()) {
        Text(
            text = localizedString(R.string.app_language),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 8.dp),
        )
        languages.forEach { locale ->
            ListItem(
                headlineContent = { Text(languageLabel(locale)) },
                supportingContent = { Text(locale) },
                trailingContent = {
                    if (locale == selected) {
                        Icon(
                            painter = painterResource(R.drawable.ic_check_black_24dp),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
                modifier = Modifier.clickableRow { onSelect(locale) },
            )
        }
    }
}

/**
 * Local languages declared by the app plus every language available in the Crowdin distribution.
 */
private fun languageOptions(context: Context): List<String> {
    val languages = mutableSetOf<String>()

    BuildConfig.AVAILABLE_LOCAL_LANGUAGE_CODES.split(';').forEach { code ->
        if (code.isNotBlank()) languages.add(code)
    }

    val supportedLanguages = Crowdin.getSupportedLanguages()
    Crowdin.getManifest()?.languages?.forEach { languageCode ->
        supportedLanguages?.get(languageCode)?.let { languages.add(it.locale) }
    }

    val current = (context.applicationContext as App).languagePreferences.getLanguageCode()
    if (current.isNotBlank()) languages.add(current)

    return languages.sorted()
}

private fun currentAppLocale(): String =
    AppCompatDelegate
        .getApplicationLocales()
        .takeIf { !it.isEmpty }
        ?.get(0)
        ?.toLanguageTag()
        ?: Locale.getDefault().toLanguageTag()

/**
 * Human readable name of a locale, taken from the Crowdin project when it is known there.
 */
private fun languageLabel(locale: String): String =
    Crowdin.getSupportedLanguages()?.values?.find { it.locale == locale }?.name
        ?: Locale.forLanguageTag(locale).displayName.ifEmpty { locale }

private fun applyLocale(
    context: Context,
    locale: String,
) {
    val preferences = (context.applicationContext as App).languagePreferences
    preferences.setLanguageCode(locale)
    // Tells BaseActivity to force a translation update once the activity is recreated.
    preferences.setLocaleChangeFlag(true)
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(locale))
}

private fun openRepository(context: Context) {
    context.startActivity(Intent(Intent.ACTION_VIEW, REPOSITORY_URL.toUri()))
}
