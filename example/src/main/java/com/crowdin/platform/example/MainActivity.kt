package com.crowdin.platform.example

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.crowdin.platform.Crowdin
import com.crowdin.platform.LoadingStateListener
import com.crowdin.platform.auth.CrowdinWebActivity
import com.crowdin.platform.example.fragments.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener, LoadingStateListener {

    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        replaceFragment(HomeFragment.newInstance())
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.post { navigationView.setCheckedItem(R.id.nav_home) }
        setTitle(R.string.home)

        Crowdin.registerDataLoadingObserver(this)
        CrowdinWebActivity.launchActivityForResult(this)
        Crowdin.registerShakeDetector(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            CrowdinWebActivity.REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "Auth success", Toast.LENGTH_SHORT).show()
                    Crowdin.connectRealTimeUpdates()
                }
            }
        }
    }

    override fun onDataChanged() {
        Crowdin.updateMenuItemsText(navigationView.menu, resources, R.menu.drawer_view)
        Log.d("Crowdin", "MainActivity: onSuccess")
    }

    override fun onFailure(throwable: Throwable) {
        Log.d("Crowdin", "MainActivity: onFailure ${throwable.localizedMessage}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Crowdin.unregisterDataLoadingObserver(this)
        Crowdin.disconnectRealTimeUpdates()
        Crowdin.unregisterShakeDetector()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_menu, menu)
        Crowdin.updateMenuItemsText(menu, resources, R.menu.activity_menu)
        return true
    }

    override fun onBackPressed() =
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                super.onBackPressed()
            }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val fragment: Fragment?
        var title = 0

        when (menuItem.itemId) {
            R.id.nav_home -> {
                fragment = HomeFragment.newInstance()
                title = R.string.home
            }
            R.id.nav_camera -> {
                fragment = CameraFragment.newInstance()
                title = R.string.camera
            }
            R.id.nav_gallery -> {
                fragment = GalleryFragment.newInstance()
                title = R.string.gallery
            }
            R.id.nav_slideshow -> {
                fragment = SlideshowFragment.newInstance()
                title = R.string.slideshow
            }
            R.id.nav_manage -> {
                fragment = ToolsFragment.newInstance()
                title = R.string.tools
            }
            R.id.nav_share -> {
                fragment = ShareFragment.newInstance()
                title = R.string.share
            }
            R.id.nav_send -> {
                fragment = SendFragment.newInstance()
                title = R.string.send
            }
            else -> fragment = null
        }

        if (fragment != null) {
            replaceFragment(fragment)
            setTitle(title)
        }

        menuItem.isChecked = true
        drawerLayout.closeDrawer(GravityCompat.START)

        return true
    }

    private fun replaceFragment(fragment: Fragment) =
            supportFragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit()
}
