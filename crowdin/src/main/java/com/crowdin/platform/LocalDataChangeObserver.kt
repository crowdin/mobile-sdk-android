package com.crowdin.platform

internal interface LocalDataChangeObserver {
    /**
     * Invoked when local data has changed
     */
    fun onDataChanged()
}
