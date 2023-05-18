@Override
public boolean onCreateOptionsMenu(Menu menu) {
    ExtentionsKt.inflateWithCrowdin(getMenuInflater(), R.menu.your_menu, menu, getResources());
    return true;
}