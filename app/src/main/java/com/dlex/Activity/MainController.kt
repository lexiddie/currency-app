package com.dlex.Activity

import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.support.v7.widget.Toolbar
import android.view.View
import com.dlex.R
import com.dlex.Fragment.*
import android.content.Intent
import android.widget.Button
import android.widget.TextView

class MainController : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val home = Home()
    private val calculator = Calculator()
    private val search = Search()
    private val histories = Histories()
    private val alerts = Alerts()

    private lateinit var navigationView: NavigationView
    private lateinit var navHeader: View
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var handler: Handler
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var tbTitle: TextView
    private lateinit var handleGesture: Button
    private lateinit var mainFragment: Fragment
    var navItemIndex = 0
    private var loadHomeOnBackPressed = true

    private var tag_home = "home"
    private var tag_calculator = "calculator"
    private var tag_search = "search"
    private var tag_histories = "histories"
    private var tag_alerts = "alerts"
    private var current_Tag = tag_home
    private var activityTitles = arrayOf(String())
    private var notificationService = NotificationService()

    override fun onResume() {
        super.onResume()
        startService(Intent(this, notificationService::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        tbTitle = toolbar.findViewById(R.id.tbTitle)
        handleGesture = toolbar.findViewById(R.id.handleGesture)
        setSupportActionBar(toolbar)

        handler = Handler()

        drawerLayout = findViewById(R.id.drawer_layout)

        navigationView = findViewById(R.id.nav_view)

        navHeader = navigationView.getHeaderView(0)

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        drawerLayout.addDrawerListener(toggle)

        navigationView.setNavigationItemSelectedListener(this)

        activityTitles = resources.getStringArray(R.array.nav_titles)

        if (savedInstanceState == null) {
            navItemIndex = 0
            current_Tag = tag_home
            loadFragment()
        }
        startService(Intent(this, notificationService::class.java))
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
            return
        }
        if (loadHomeOnBackPressed) {
            if (navItemIndex != 0) {
                navItemIndex = 0
                current_Tag = tag_home
                loadFragment()
                return
            }
        }
        super.onBackPressed()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                navItemIndex = 0
                current_Tag = tag_home
                loadFragment()
            }
            R.id.nav_calculator -> {
                navItemIndex = 1
                current_Tag = tag_calculator
                loadFragment()
            }
            R.id.nav_search -> {
                navItemIndex = 2
                current_Tag = tag_search
                loadFragment()
            }
            R.id.nav_histories -> {
                navItemIndex = 3
                current_Tag = tag_histories
                loadFragment()
            }
            R.id.nav_alerts -> {
                navItemIndex = 4
                current_Tag = tag_alerts
                loadFragment()
            }
            R.id.nav_privacy -> {
                startActivity(Intent(this, PrivacyController::class.java))
                drawerLayout.closeDrawers()
                return true
            }
            R.id.nav_help -> {
                startActivity(Intent(this, HelpController::class.java))
                drawerLayout.closeDrawers()
                return true
            }
            R.id.nav_about -> {
                startActivity(Intent(this, AboutController::class.java))
                drawerLayout.closeDrawers()
                return true
            }
            R.id.nav_ads -> {
                startActivity(Intent(this, AdsController::class.java))
                drawerLayout.closeDrawers()
                return true
            }
            else -> navItemIndex = 0
        }
        return true
    }

    fun loadFragment() {
        selectNavMenu()
        setToolbarTitle()
        if (getSupportFragmentManager().findFragmentByTag(current_Tag) != null) {
            drawerLayout.closeDrawers()
            return
        }

        val pendingRunnable = Runnable {
            this.run {
                mainFragment = getFragment()
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                fragmentTransaction.replace(R.id.frame, mainFragment, current_Tag)
                fragmentTransaction.commit()
                drawerLayout.closeDrawers()
            }
        }

        handler.post(pendingRunnable)
        invalidateOptionsMenu()
    }


    private fun getFragment(): Fragment {
        when (navItemIndex) {
            0 -> {
                home.mainController = this
                home.handleGesture = handleGesture
                return home
            }
            1 -> {
                calculator.mainController = this
                return calculator
            }
            2 -> {
                search.mainController = this
                search.handleGesture = handleGesture
                return search
            }
            3 -> {
                histories.mainController = this
                return histories
            }
            4 -> {
                alerts.mainController = this
                alerts.handleGesture = handleGesture
                return alerts
            }
            else -> return home
        }
    }

    private fun setToolbarTitle() {
        supportActionBar!!.setTitle("")
        tbTitle.text = activityTitles[navItemIndex]
        if (navItemIndex == 0) {
            handleGesture.visibility = View.VISIBLE
            handleGesture.setBackgroundResource(R.drawable.save)
        } else if (navItemIndex == 4) {
            handleGesture.visibility = View.VISIBLE
            handleGesture.setBackgroundResource(R.drawable.alert)
        } else if (navItemIndex == 2) {
            handleGesture.visibility = View.GONE
            handleGesture.setBackgroundResource(R.drawable.save)
        }else {
            handleGesture.visibility = View.GONE
        }

    }



    private fun selectNavMenu() {
        navigationView.menu.getItem(navItemIndex).setChecked(true)
    }
}
