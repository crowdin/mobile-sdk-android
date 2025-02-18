Crowdin.init(this,
    crowdinConfig,
    new LoadingStateListener() {
        @Override
        public void onDataChanged() {
            // Called when new translation data is updated
        }

        @Override
        public void onFailure(Throwable throwable) {
            // Handle failures
        }
    });