@Override
protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(Crowdin.wrapContext(newBase));
}