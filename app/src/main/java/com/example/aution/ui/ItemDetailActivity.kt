package com.example.aution.ui.detail

import CreateHistoryAuction
import HistoryAuction
import HistoryAuctionFetcher
import ItemFetcher
import UserDbHelper
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.aution.MainActivity
import com.example.aution.R
import com.example.aution.databinding.ActivityItemDetailBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ItemDetailActivity : AppCompatActivity() {
    private lateinit var userDbHelper: UserDbHelper
    private lateinit var binding: ActivityItemDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userDbHelper = UserDbHelper(this)
        // Retrieve the ID passed to this Activity
        val itemId = intent.getIntExtra("ITEM_ID", 1)
        lifecycleScope.launch {
            val item = ItemFetcher().fetchItemById(itemId)
            item?.let {
                Glide.with(this@ItemDetailActivity).load(it.image).into(binding.bannerImageView)
            }

            // Access the LinearLayout
            val itemListLayout: LinearLayout = binding.itemListLayout
            itemListLayout.removeAllViews()

            val historys = HistoryAuctionFetcher().getHistoryAuctionsByItem(itemId)
            historys?.let {
                addItemToScrollView(it, itemListLayout)
            }
        }
        var bidButton = findViewById<Button>(R.id.bidButton)
        var clearButton = findViewById<Button>(R.id.clearButton)
        var numberInput = findViewById<EditText>(R.id.editTextNumberSigned)

        clearButton.setOnClickListener {
            numberInput.setText("")
            numberInput.clearFocus()
        }
        val userCursor = userDbHelper.getOneUser()
        userCursor?.let { cursor ->
            if (cursor.moveToFirst()) {
                val userIdIndex =
                    cursor.getColumnIndex(UserDbHelper.COLUMN_ID) // or the name of your ID column
                val userId = cursor.getInt(userIdIndex)

                bidButton.setOnClickListener {
                    val numberAsString = numberInput.text.toString()
                    val number: Int? = numberAsString.toIntOrNull()
                    if (number != null) {
                        lifecycleScope.launch {
                            HistoryAuctionFetcher().postHistoryAuction(
                                CreateHistoryAuction(
                                    price = number, user_id = userId, item_id = itemId
                                )
                            )
                            // Access the LinearLayout
                            val itemListLayout: LinearLayout = binding.itemListLayout
                            itemListLayout.removeAllViews()

                            val historys = HistoryAuctionFetcher().getHistoryAuctionsByItem(itemId)
                            historys?.let {
                                addItemToScrollView(it, itemListLayout)
                            }
                        }

                    } else {
                        // Handle the case where the EditText was empty
                        Toast.makeText(this, "Please enter a number!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            cursor.close()
        }

    }

    private fun formatTime(originalTime: String): String {
        try {
            // Adjust the original format to match the input string
            val originalFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())

            // Define the desired format
            val targetFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            // Parse the original time string to a Date object
            val date = originalFormat.parse(originalTime)

            // Check if the date was parsed successfully
            if (date != null) {
                // Format the Date object to the desired format and return it
                return targetFormat.format(date)
            }
        } catch (e: Exception) {
            // Handle the potential parsing exception
            e.printStackTrace()
        }

        // Return the original time if parsing failed or an empty string or a default message
        return "Invalid date format"
    }

    private fun addItemToScrollView(historys: List<HistoryAuction>, itemListLayout: LinearLayout) {
        // Loop through each history item and create a TextView for it
        historys.forEach { history ->
            // Create a new TextView and set its properties
            var time = formatTime(history.time_auction)
            val newItem = TextView(this).apply {
                text = "User ID: ${history.user_id}, Price: ${history.price}, Time: ${time}"
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    // Add margins and padding for better spacing and readability
                    setMargins(16, 16, 16, 16)
                }
                // Adjust text appearance, e.g., text size and color (optional)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                setTextColor(
                    ContextCompat.getColor(
                        this@ItemDetailActivity, R.color.black
                    )
                ) // Adjust the color as needed
                // Add padding inside the TextView for better text alignment
                setPadding(16, 16, 16, 16)
            }

            // Add the new TextView to the parent LinearLayout
            itemListLayout.addView(newItem)
        }
    }

}
