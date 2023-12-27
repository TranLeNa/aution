package com.example.aution.ui.notifications

import UserDbHelper
import UserFetcher
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.aution.MainActivity
import com.example.aution.databinding.ActivityUserBinding
import kotlinx.coroutines.launch

class NotificationsFragment : Fragment() {
    private lateinit var userDbHelper: UserDbHelper
    private var _binding: ActivityUserBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)
        // Initialize UserDbHelper
        userDbHelper = UserDbHelper(requireContext())
        _binding = ActivityUserBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val userCursor = userDbHelper.getOneUser()

        userCursor?.let { cursor ->
            if (cursor.moveToFirst()) {
                val userIdIndex =
                    cursor.getColumnIndex(UserDbHelper.COLUMN_ID) // or the name of your ID column
                val userId = cursor.getInt(userIdIndex)

                lifecycleScope.launch {
                    var user = UserFetcher().fetchUserById(userId)
                    user?.let {
                        binding.textName.text = user.name
                        binding.textEmail.text = user.email
                    }
                    user?.avatar?.let { avatarBase64 ->
                        val bitmap = decodeBase64ToBitmap(avatarBase64)
                        bitmap?.let {
                            Glide.with(this@NotificationsFragment).load(bitmap)
                                .into(binding.bannerImageView)
                        }
                    }
                }



                binding.button.setOnClickListener {
                    userDbHelper.removeAllUsers()
                    Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                }
            }
            cursor.close()
        }



        return root
    }

    private fun decodeBase64ToBitmap(base64Str: String): Bitmap? {
        return try {
            val decodedBytes =
                Base64.decode(base64Str.substring(base64Str.indexOf(",") + 1), Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            Log.e(
                "NotificationsFragment", "Failed to decode Base64 string, Exception: ${e.message}"
            )
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}