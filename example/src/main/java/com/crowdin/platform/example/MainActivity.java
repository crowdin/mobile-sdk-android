package com.crowdin.platform.example;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.crowdin.platform.example.fragments.CameraFragment;
import com.crowdin.platform.example.fragments.GalleryFragment;
import com.crowdin.platform.example.fragments.HomeFragment;
import com.crowdin.platform.example.fragments.SendFragment;
import com.crowdin.platform.example.fragments.ShareFragment;
import com.crowdin.platform.example.fragments.SlideshowFragment;
import com.crowdin.platform.example.fragments.ToolsFragment;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        replaceFragment(HomeFragment.newInstance());
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.post(() -> navigationView.setCheckedItem(R.id.nav_home));
        setTitle(R.string.home);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment;
        int title = 0;

        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                fragment = HomeFragment.newInstance();
                title = R.string.home;
                break;
            case R.id.nav_camera:
                fragment = CameraFragment.newInstance();
                title = R.string.camera;
                break;
            case R.id.nav_gallery:
                fragment = GalleryFragment.newInstance();
                title = R.string.gallery;
                break;
            case R.id.nav_slideshow:
                fragment = SlideshowFragment.newInstance();
                title = R.string.slideshow;
                break;
            case R.id.nav_manage:
                fragment = ToolsFragment.newInstance();
                title = R.string.tools;
                break;
            case R.id.nav_share:
                fragment = ShareFragment.newInstance();
                title = R.string.share;
                break;
            case R.id.nav_send:
                fragment = SendFragment.newInstance();
                title = R.string.send;
                break;
            default:
                fragment = null;
                break;
        }

        if (fragment != null) {
            replaceFragment(fragment);
            setTitle(title);
        }

        menuItem.setChecked(true);
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }
}
