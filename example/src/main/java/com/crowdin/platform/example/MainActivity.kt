package com.crowdin.platform.example

import android.os.Bundle
import android.os.Handler
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.crowdin.crowdin_controls.destroyCrowdinControl
import com.crowdin.crowdin_controls.initCrowdinControl
import com.crowdin.platform.example.category.CategoryFragment
import com.crowdin.platform.example.task.fragment.DashboardFragment
import com.crowdin.platform.example.task.fragment.HistoryFragment
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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

//      Init Crowdin SDK overlay controls
        initCrowdinControl(this)
    }

    override fun onDestroy() {
        super.onDestroy()
//      Destroy crowdin overlay view.
        destroyCrowdinControl(this)
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawerLayout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
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
}
