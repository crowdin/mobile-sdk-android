@Override
protected void onDestroy() {
    super.onDestroy();
    CrowdinControlUtil.destroyCrowdinControl(this);
}