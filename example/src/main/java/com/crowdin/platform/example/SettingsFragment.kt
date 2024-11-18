package com.crowdin.platform.example

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.crowdin.platform.Crowdin
import com.crowdin.platform.example.task.OnItemSelectedListener
import com.crowdin.platform.example.utils.updateLocale
import java.util.Locale

class SettingsFragment : Fragment(), OnItemSelectedListener.SpinnerItemListener {

    private lateinit var spinnerLanguages: Spinner

    private lateinit var languageDescription: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setTitle(R.string.settings)
        spinnerLanguages = view.findViewById(R.id.spinnerLanguages)
        languageDescription = view.findViewById(R.id.languageDescriptionTv)
        loadDataInSpinner()
    }

    private fun loadDataInSpinner() {
        val languagePreferences = (requireActivity().application as App).languagePreferences
        val manifestData = Crowdin.getManifest()
        val labels = mutableListOf<String>()
        labels.add(languagePreferences.getLanguageCode())
        manifestData?.languages?.forEach { languageIndex ->
            Crowdin.getSupportedLanguages()?.data?.find { supportedLanguage ->
                supportedLanguage.data.id == languageIndex
            }?.let { languageInfoData ->
                if (!labels.contains(languageInfoData.data.locale)) {
                    labels.add(languageInfoData.data.locale)
                }
            }
        }

        BuildConfig.AVAILABLE_LOCAL_LANGUAGE_CODES.split(';').forEach {
            if (!labels.contains(it)) {
                labels.add(it)
            }
        }

        val dataAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, labels)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLanguages.adapter = dataAdapter
        spinnerLanguages.onItemSelectedListener = OnItemSelectedListener(this)
    }

    override fun onSpinnerItemSelected(item: String) {
        Crowdin.getSupportedLanguages()?.data?.find { languageInfoData ->
            languageInfoData.data.locale == item
        }?.data?.let { languageDescription.text = it.name }

        val defaultLocale = Locale.getDefault()
        if (item == "${defaultLocale.language}-${defaultLocale.country}") {
            return
        }

        requireContext().updateLocale(item)

        // Save new language code to storage. We will use it on app launch to replace default locale before SDK initialization.
        (requireActivity().application as App).languagePreferences.setLanguageCode(item)

        // Reload data from Crowdin for new selected language.
        Crowdin.forceUpdate(requireContext())
    }
}
