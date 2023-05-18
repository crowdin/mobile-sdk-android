override fun onDestroy() {
    super.onDestroy()
    Crowdin.disconnectRealTimeUpdates()
}