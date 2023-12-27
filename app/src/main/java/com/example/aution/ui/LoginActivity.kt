package com.example.aution.ui.detail

import HistoryAuction
import HistoryAuctionFetcher
import Login
import UserDbHelper
import UserFetcher
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.aution.MainActivity
import com.example.aution.R
import com.example.aution.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var userDbHelper: UserDbHelper
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // Initialize UserDbHelper
        userDbHelper = UserDbHelper(this)

        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var loginButton = findViewById<Button>(R.id.button)
        var textEmail = findViewById<EditText>(R.id.textEmail)
        var textPassword = findViewById<EditText>(R.id.editTextPasswordAddress)
        var buttonSignUp = findViewById<Button>(R.id.signup)

        buttonSignUp.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java).apply {
            }
            startActivity(intent)
        }
        loginButton.setOnClickListener {
            Toast.makeText(
                this, "Email: ${textEmail.text}\nPassword: ${textPassword.text}", Toast.LENGTH_SHORT
            ).show()
            //         Retrieve the ID passed to this Activity
            lifecycleScope.launch {
                val user = UserFetcher().login(
                    Login(
                        email = textEmail.text.toString(), password = textPassword.text.toString()
                    )
                )
                if (user != null) {
                    Log.e("CategoryFetcher", "Error: ${user}")
                    userDbHelper.addUser(user.id, user.email, textPassword.text.toString())
                    val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    finish()
                }


            }
        }


    }

    private fun addItemToScrollView(historys: List<HistoryAuction>, itemListLayout: LinearLayout) {
        historys.forEach { history ->
            val newItem = TextView(this)  // Use 'this' for context as this is an Activity
            // Assuming user_id is an Int, converting it to String
            newItem.text =
                "User ID: ${history.user_id}, Price: ${history.price}, Time: ${history.time_auction}"
            newItem.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            itemListLayout.addView(newItem)
        }
    }
}
