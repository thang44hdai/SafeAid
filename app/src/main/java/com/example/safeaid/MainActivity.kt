package com.example.safeaid

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.androidtraining.R
import com.example.androidtraining.databinding.ActivityMainBinding
import com.example.safeaid.core.ui.BaseContainerFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    private val mainNavigator: MainNavigator by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        setUpNav()
        observeNavigator()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun setUpNav() {
        val navigation = R.navigation.nav_main
        val navController = navHostFragment.findNavController()
        val graphInflater = navHostFragment.navController.navInflater
        val navGraph = graphInflater.inflate(navigation)
        navHostFragment.navController.setGraph(navGraph, null)

        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            when (destination.id) {
                R.id.mainScreen -> {
                }
            }
        }
        navController.addOnDestinationChangedListener(listener)
    }

    private fun observeNavigator() {
        lifecycleScope.launch {
            for (event in mainNavigator.navigation) {
                onNavigationEvent(event)
            }
        }
    }

    private fun onNavigationEvent(event: BaseContainerFragment.NavigationEvent) {
        val navController = navHostFragment.findNavController()
        when (event) {

        }
    }
}