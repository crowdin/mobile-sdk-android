package com.crowdin.platform.example;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.icu.text.PluralRules;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.crowdin.platform.Crowdin;
import com.crowdin.platform.api.ArrayData;
import com.crowdin.platform.api.LanguageData;
import com.crowdin.platform.api.PluralData;
import com.crowdin.platform.example.fragments.CameraFragment;
import com.crowdin.platform.example.fragments.GalleryFragment;
import com.crowdin.platform.example.fragments.HomeFragment;
import com.crowdin.platform.example.fragments.SendFragment;
import com.crowdin.platform.example.fragments.ShareFragment;
import com.crowdin.platform.example.fragments.SlideshowFragment;
import com.crowdin.platform.example.fragments.ToolsFragment;
import com.crowdin.platform.utils.LocaleUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        saveLanguageData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_menu, menu);
        Crowdin.updateMenuItemsText(menu, getResources(), R.menu.activity_menu);
        return true;
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

    private void saveLanguageData() {
        LanguageData languageData = new LanguageData("de");
        Map<String, String> strings = saveIntoRepositoryProjectStrings();
        languageData.setResources(strings);

        List<ArrayData> arrays = saveIntoRepositoryProjectStringArrays();
        languageData.setArrays(arrays);

        List<PluralData> plurals = saveIntoRepositoryProjectPlurals();
        languageData.setPlurals(plurals);

        SharedPreferences sharedPreferences = getSharedPreferences("test", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("FIRST_LAUNCH", true)) {
            Crowdin.saveLanguageData(languageData);
            sharedPreferences.edit().putBoolean("FIRST_LAUNCH", false).apply();
        }
    }

    private Map<String, String> saveIntoRepositoryProjectStrings() {
        Map<String, String> strings = new HashMap<>();
        Field[] fields = R.string.class.getFields();
        for (final Field field : fields) {
            String name = field.getName();
            try {
                int id = field.getInt(R.string.class);
                String value = getString(id);
                if (!value.contains("DE")) {
                    strings.put(name, "DE " + value);
                }
            } catch (Exception ex) {
            }
        }

        return strings;
    }

    private List<ArrayData> saveIntoRepositoryProjectStringArrays() {
        List<ArrayData> arrays = new ArrayList<>();
        Field[] fields = R.array.class.getFields();
        for (final Field field : fields) {
            String name = field.getName();
            try {
                boolean updated = false;
                int id = field.getInt(R.string.class);
                String[] values = getResources().getStringArray(id);
                for (int i = 0; i < values.length; i++) {
                    if (!values[i].contains("DE")) {
                        values[i] = "DE" + values[i];
                        updated = true;
                    }
                }

                if (updated) {
                    ArrayData arrayData = new ArrayData();
                    arrayData.setName(name);
                    arrayData.setValues(values);
                    arrays.add(arrayData);
                }

            } catch (Exception ex) {
            }
        }

        return arrays;
    }

    @TargetApi(Build.VERSION_CODES.N)
    private List<PluralData> saveIntoRepositoryProjectPlurals() {
        List<PluralData> plurals = new ArrayList<>();
        Field[] fields = R.plurals.class.getFields();
        for (final Field field : fields) {
            String name = field.getName();
            try {
                boolean updated = false;
                int id = field.getInt(R.string.class);
                Map<String, String> quantity = new HashMap<>();

                for (int i = 0; i < 31; i++) {
                    PluralRules rule = PluralRules.forLocale(LocaleUtils.getCurrentLocale());
                    String ruleName = rule.select(i);
                    String value = getResources().getQuantityString(id, i);

                    if (!value.contains("DE")) {
                        updated = true;
                        quantity.put(ruleName, "DE" + value);
                    }
                }

                if (updated) {
                    PluralData pluralData = new PluralData();
                    pluralData.setName(name);
                    pluralData.setQuantity(quantity);
                    plurals.add(pluralData);
                }

            } catch (Exception ex) {
            }
        }

        return plurals;
    }
}
