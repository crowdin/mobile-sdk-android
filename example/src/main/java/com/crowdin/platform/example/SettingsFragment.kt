package com.crowdin.platform.example

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import com.crowdin.platform.Crowdin
import com.crowdin.platform.example.task.OnItemSelectedListener
import java.util.Locale

class SettingsFragment : Fragment(), OnItemSelectedListener.SpinnerItemListener {

    private lateinit var spinnerLanguages: Spinner
    private lateinit var languageDescription: TextView

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View? = inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setTitle(R.string.settings)
        spinnerLanguages = view.findViewById(R.id.spinnerLanguages)
        languageDescription = view.findViewById(R.id.languageDescriptionTv)
        setupLanguageSpinner()
    }

    private fun setupLanguageSpinner() {
        val languageList = getLanguageList()

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languageList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLanguages.adapter = adapter

        // Set current selection
        val currentPosition = findCurrentLanguagePosition(languageList)
        spinnerLanguages.setSelection(currentPosition)

        spinnerLanguages.onItemSelectedListener = OnItemSelectedListener(this)
    }

    private fun getLanguageList(): List<String> {
        val languageSet = mutableSetOf<String>()

        // Add available local language codes
        BuildConfig.AVAILABLE_LOCAL_LANGUAGE_CODES.split(';').forEach { code ->
            if (code.isNotBlank()) languageSet.add(code)
        }

        // Add supported languages from Crowdin
        Crowdin.getManifest()?.languages?.forEach { languageIndex ->
            Crowdin.getSupportedLanguages()?.data?.find { it.data.id == languageIndex }
                ?.let { languageSet.add(it.data.locale) }
        }

        // Add current language preference as fallback
        val currentLang = (requireActivity().application as App).languagePreferences.getLanguageCode()
        if (currentLang.isNotBlank()) languageSet.add(currentLang)

        return languageSet.sorted()
    }

    private fun findCurrentLanguagePosition(languageList: List<String>): Int =
        languageList.indexOf(getCurrentAppLocale()).takeIf { it != -1 } ?: 0

    private fun getCurrentAppLocale(): String =
        AppCompatDelegate.getApplicationLocales()
            .takeIf { !it.isEmpty }?.get(0)?.toLanguageTag() ?: Locale.getDefault().toLanguageTag()

    override fun onSpinnerItemSelected(item: String) {
        if (getCurrentAppLocale() == item) {
            updateLanguageDescription(item)
            return
        }

        // Save to preferences and set new locale
        (requireActivity().application as App).languagePreferences.setLanguageCode(item)
        // Mark that we're about to change locale
        (requireActivity().application as App).languagePreferences.setLocaleChangeFlag(true)

        updateLanguageDescription(item)
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(item))
    }

    private fun updateLanguageDescription(item: String) {
        Crowdin.getSupportedLanguages()?.data?.find { it.data.locale == item }
            ?.data?.let { languageDescription.text = it.name }
    }
}
