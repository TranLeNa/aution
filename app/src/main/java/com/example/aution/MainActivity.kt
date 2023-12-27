package com.example.aution

import UserDbHelper
import android.content.Intent
import android.os.Bundle
import android.database.Cursor
import android.util.Log
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.aution.databinding.ActivityMainBinding
import com.example.aution.ui.detail.ItemDetailActivity
import com.example.aution.ui.detail.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userDbHelper: UserDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize UserDbHelper
        userDbHelper = UserDbHelper(this)

        // Check if at least one user exists
        checkUserExists()

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_history
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navController.navigate(R.id.navigation_dashboard)
    }

    private fun checkUserExists() {
        val cursor: Cursor? = userDbHelper.getOneUser()
        if (cursor != null) {
            if (!cursor.moveToFirst()) {  // If the cursor is empty
                val intent = Intent(this, LoginActivity::class.java).apply {}
                startActivity(intent)
                Toast.makeText(this, "Please log in.", Toast.LENGTH_LONG).show()
            } else {
                Log.e("checkUserExists", "Error: ${cursor.moveToFirst()}")
            }
        }
        cursor?.close()  // Close the cursor to avoid memory leaks
    }
}
