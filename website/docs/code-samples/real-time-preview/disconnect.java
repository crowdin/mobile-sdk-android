@Override
protected void onDestroy() {
    super.onDestroy();
    Crowdin.disconnectRealTimeUpdates();
}