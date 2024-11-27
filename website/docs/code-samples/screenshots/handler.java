View.OnClickListener oclBtnOk = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Crowdin.sendScreenshot(YourActivity.this, screenshotName, new ScreenshotCallback() {
            @Override
            public void onSuccess() {
                Log.d("", "Screenshot uploaded");
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d("", String.valueOf(throwable));
            }
      });
  }
};