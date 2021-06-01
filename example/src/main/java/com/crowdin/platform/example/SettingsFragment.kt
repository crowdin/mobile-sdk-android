package com.crowdin.platform.example

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.crowdin.platform.Crowdin
import com.crowdin.platform.example.task.OnItemSelectedListener
import com.crowdin.platform.example.utils.updateLocale
import java.util.Locale

class SettingsFragment : Fragment(), OnItemSelectedListener.SpinnerItemListener {

    private lateinit var spinnerLanguages: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinnerLanguages = view.findViewById(R.id.spinnerLanguages)
        loadDataInSpinner()
    }

    private fun loadDataInSpinner() {

        val languagePreferences = (requireActivity().application as App).languagePreferences

        val manifestData = Crowdin.getManifest()

        val labels = mutableListOf<String>()

        manifestData?.languages?.let { labels.addAll(it) }

        val savedLanguageCode = languagePreferences.getLanguageCode()

        BuildConfig.AVAILABLE_LANGUAGES.split(',').forEach {
            if (!labels.contains(it)) {
                labels.add(it)
            }
        }

        if (!labels.contains(savedLanguageCode)) {
            labels.add(0, savedLanguageCode)
        }

        if (!labels.contains(languagePreferences.getDefaultLanguageCode())) {
            labels.add(0, languagePreferences.getDefaultLanguageCode())
        }


        val dataAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, labels)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLanguages.adapter = dataAdapter
        spinnerLanguages.onItemSelectedListener = OnItemSelectedListener(this)
        spinnerLanguages.setSelection(dataAdapter.getPosition(savedLanguageCode))
    }

    override fun onSpinnerItemSelected(item: String) {
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
