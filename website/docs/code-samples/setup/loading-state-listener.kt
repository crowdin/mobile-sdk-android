Crowdin.init(applicationContext,
    crowdinConfig,
    loadingStateListener = object : LoadingStateListener {
        override fun onDataChanged() {
            // Called when new translation data is updated
        }

        override fun onFailure(throwable: Throwable) {
            // Handle failures
        }
    })