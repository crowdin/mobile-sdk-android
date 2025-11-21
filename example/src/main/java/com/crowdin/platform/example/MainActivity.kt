package com.crowdin.platform.example

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.crowdin.platform.Crowdin
import com.crowdin.platform.LoadingStateListener
import com.crowdin.platform.util.getLocale
import com.crowdin.platform.util.inflateWithCrowdin
import com.example.example_info.InfoActivity
import com.google.android.material.navigation.NavigationView

class MainActivity : BaseActivity(), LoadingStateListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
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
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment? ?: return

        // Set up Action Bar
        val navController = host.navController
        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_dashboard), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navigationView.setupWithNavController(navController)
        val header = navigationView.getHeaderView(0)
        header.findViewById<TextView>(R.id.textView).movementMethod =
            LinkMovementMethod.getInstance()

        // Register data observer. When data loaded you can invalidate your UI to apply new resources.
        Crowdin.registerDataLoadingObserver(this)

        // Test of public API that returns string by key or empty if not found
        val languageTag = this.resources.configuration.getLocale().toLanguageTag()
        Crowdin.getString(languageTag, "settings")
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
        return when (item.itemId) {
            R.id.menuInfo -> {
                startActivity(Intent(this, InfoActivity::class.java))
                true
            }

            R.id.menuCompose -> {
                startActivity(Intent(this, SampleComposeActivity::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean = findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration)

    override fun onDataChanged() {
        invalidateOptionsMenu()
        Crowdin.updateMenuItemsText(R.menu.activity_main_drawer, navigationView.menu, resources)
    }

    override fun onFailure(throwable: Throwable) {
    }
}
