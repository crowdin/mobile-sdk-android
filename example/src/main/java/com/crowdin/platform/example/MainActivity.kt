package com.crowdin.platform.example

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.crowdin.platform.Crowdin
import com.crowdin.platform.LoadingStateListener
import com.crowdin.platform.example.category.CategoryFragment
import com.crowdin.platform.example.task.fragment.DashboardFragment
import com.crowdin.platform.example.task.fragment.HistoryFragment
import com.crowdin.platform.util.inflateWithCrowdin
import com.google.android.material.navigation.NavigationView

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener,
    LoadingStateListener {

    private lateinit var toolbarMain: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbarMain = findViewById(R.id.toolbarMain)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        setSupportActionBar(toolbarMain)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbarMain,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.setDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)

        val header = navigationView.getHeaderView(0)
        header.findViewById<TextView>(R.id.textView).movementMethod =
            LinkMovementMethod.getInstance()

        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, DashboardFragment())
            .commit()

        // Register data observer. When data loaded you can invalidate your UI to apply new resources.
        Crowdin.registerDataLoadingObserver(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        Crowdin.unregisterDataLoadingObserver(this)
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawerLayout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Translate menu items
        menuInflater.inflateWithCrowdin(R.menu.activity_menu, menu, resources)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.menuInfo) {
            startActivity(Intent().apply {
                setClassName(this@MainActivity, "com.example.example_info.InfoActivity")
            })

            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Handler().postDelayed({ navigate(item.itemId) }, 280)
        return true
    }

    private fun navigate(id: Int) {
        val fragment = when (id) {
            R.id.nav_dashboard -> {
                toolbarMain.title = getString(R.string.dashboard)
                DashboardFragment()
            }
            R.id.nav_category -> {
                toolbarMain.title = getString(R.string.category)
                CategoryFragment()
            }
            R.id.nav_history -> {
                toolbarMain.title = getString(R.string.history)
                HistoryFragment()
            }
            R.id.nav_settings -> {
                toolbarMain.title = getString(R.string.settings)
                SettingsFragment()
            }
            else -> DashboardFragment()
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameLayout, fragment).commit()
    }

    override fun onDataChanged() {
        invalidateOptionsMenu()
        Crowdin.updateMenuItemsText(R.menu.activity_main_drawer, navigationView.menu, resources)
    }

    override fun onFailure(throwable: Throwable) {

    }
}
