@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    CrowdinControlUtil.onActivityResult(this, requestCode);
}