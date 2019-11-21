package com.crowdin.platform.util

import android.content.res.Resources
import android.view.Menu
import android.view.MenuInflater
import androidx.annotation.MenuRes
import com.crowdin.platform.Crowdin

fun MenuInflater.inflateWithCrowdin(@MenuRes menuRes: Int, menu: Menu, resources: Resources) {
    this.inflate(menuRes, menu)
    Crowdin.updateMenuItemsText(menuRes, menu, resources)
}
