package com.example.aution.ui.detail

import HistoryAuction
import SignupDetails
import UserDbHelper
import UserFetcher
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.aution.MainActivity
import com.example.aution.R
import com.example.aution.databinding.ActivitySignupBinding
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class SignUpActivity : AppCompatActivity() {
    private lateinit var userDbHelper: UserDbHelper
    private lateinit var binding: ActivitySignupBinding

    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        // Initialize UserDbHelper
        userDbHelper = UserDbHelper(this)

        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loginButton = findViewById<Button>(R.id.button)
        val textEmail = findViewById<EditText>(R.id.textEmail)
        val textPassword = findViewById<EditText>(R.id.editTextPasswordAddress)
        val textName = findViewById<EditText>(R.id.editTextName)
        val imageViewAvatar = findViewById<ImageView>(R.id.imageViewAvatar)
        val uploadAvatarButton = findViewById<Button>(R.id.buttonFileupload)

        uploadAvatarButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            try {
                startActivityForResult(
                    Intent.createChooser(intent, "Select a file to upload"), PICK_IMAGE_REQUEST
                )
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(
                    this@SignUpActivity, "Please install a File Manager.", Toast.LENGTH_SHORT
                ).show()
            }
        }

        loginButton.setOnClickListener {
            val bitmap = (imageViewAvatar.drawable as BitmapDrawable).bitmap
            val base64Image = convertToBase64(bitmap)
            Log.e("image", base64Image)

            lifecycleScope.launch {
                val user = UserFetcher().signup(
                    SignupDetails(
                        email = textEmail.text.toString(),
                        password = textPassword.text.toString(),
                        name = textName.text.toString(),
                        avatar = base64Image
                    )
                )
                if (user != null) {
                    Log.e("UserFetcher", "Error: $user")
                    val intent = Intent(this@SignUpActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    finish()
                }


            }

            // ... Rest of your login code ...
        }
    }

    private fun convertToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val uri: Uri? = data.data
            findViewById<ImageView>(R.id.imageViewAvatar).setImageURI(uri)
        }
    }

    private fun addItemToScrollView(historys: List<HistoryAuction>, itemListLayout: LinearLayout) {
        historys.forEach { history ->
            val newItem = TextView(this)
            newItem.text =
                "User ID: ${history.user_id}, Price: ${history.price}, Time: ${history.time_auction}"
            newItem.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            itemListLayout.addView(newItem)
        }
    }
}
