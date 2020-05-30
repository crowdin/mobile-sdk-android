package com.crowdin.platform.example

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.crowdin.platform.Crowdin
import com.crowdin.platform.LoadingStateListener
import com.crowdin.platform.example.fragments.CameraFragment
import com.crowdin.platform.example.fragments.GalleryFragment
import com.crowdin.platform.example.fragments.HomeFragment
import com.crowdin.platform.example.fragments.SendFragment
import com.crowdin.platform.example.fragments.ShareFragment
import com.crowdin.platform.example.fragments.SlideshowFragment
import com.crowdin.platform.example.fragments.ToolsFragment
import com.crowdin.platform.util.inflateWithCrowdin
import com.example.crowdin_controls.destroyCrowdinControl
import com.example.crowdin_controls.initCrowdinControl
import com.example.crowdin_controls.onActivityResult
import com.google.android.material.navigation.NavigationView

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout

    private val dataLoadingObserver = object : LoadingStateListener {
        override fun onDataChanged() {
            Log.d(MainActivity::class.java.simpleName, "LoadingStateListener: onSuccess")
        }

        override fun onFailure(throwable: Throwable) {
            Log.d(
                MainActivity::class.java.simpleName,
                "LoadingStateListener: onFailure ${throwable.localizedMessage}"
            )
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        replaceFragment(HomeFragment.newInstance())
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.post { navigationView.setCheckedItem(R.id.nav_home) }
        setTitle(R.string.home)

        // Observe data loading.
        Crowdin.registerDataLoadingObserver(dataLoadingObserver)

        // Simple device shake detector. Could be used for triggering force update.
        Crowdin.registerShakeDetector(this)

//      Java
//      CrowdinControlUtil.initCrowdinControl(this);

        // Init Crowdin SDK overlay controls
        initCrowdinControl(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onActivityResult(this, requestCode)

//      Java
//      CrowdinControlUtil.onActivityResult(this, requestCode);
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove data loading observer.
        Crowdin.unregisterDataLoadingObserver(dataLoadingObserver)

        // Remove shake detector listener.
        Crowdin.unregisterShakeDetector()

        // Destroy crowdin overlay view.
        destroyCrowdinControl(this)
//      Java
//      CrowdinControlUtil.destroyCrowdinControl(this);
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Translate menu items
        menuInflater.inflateWithCrowdin(R.menu.activity_menu, menu, resources)
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
