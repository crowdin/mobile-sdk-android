package com.crowdin.platform.example

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.crowdin.platform.Crowdin
import com.crowdin.platform.example.task.OnItemSelectedListener
import com.crowdin.platform.example.utils.updateLocale
import kotlinx.android.synthetic.main.fragment_settings.*
import java.util.Locale

class SettingsFragment : Fragment(), OnItemSelectedListener.SpinnerItemListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadDataInSpinner()
    }

    private fun loadDataInSpinner() {
        // Test languages. You can set your own depending on you project supported languages.
        val labels: List<String> = arrayListOf(
            (requireActivity().application as App).languagePreferences.getLanguageCode(),
            "en-US",
            "de-DE",
            "uk-UA",
            "fr-FR",
            "ar-BH",
            "ar-EG",
            "ar-SA",
            "ar-YE",
            "en-co",
            "it",
            "es-ES",
            "es-SV"
        )

        val dataAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, labels)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLanguages.adapter = dataAdapter
        spinnerLanguages.onItemSelectedListener = OnItemSelectedListener(this)
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
