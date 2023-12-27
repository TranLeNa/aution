package com.example.aution.ui.home

import HistoryAuction
import HistoryAuctionFetcher
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.aution.R
import com.example.aution.databinding.FragmentHistoryBinding
import kotlinx.coroutines.launch

// Make sure HistoryAuction is correctly imported and defined somewhere in your project

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)

        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        // Access the LinearLayout
        val itemListLayout: LinearLayout = binding.itemListLayout
        itemListLayout.removeAllViews()

        lifecycleScope.launch {
            var historys = HistoryAuctionFetcher().fetchHistoryAuctions()
            if (historys != null) {
                addItemToScrollView(historys, itemListLayout)
            }
        }
        return root
    }


    private fun addItemToScrollView(historys: List<HistoryAuction>, itemListLayout: LinearLayout) {
        // Loop through each history item and create a TextView for it
        historys.forEach { history ->
            // Create a new TextView and set its properties
            val newItem = TextView(context).apply {
                text =
                    "User ID: ${history.user_id}, Price: ${history.price}, Time: ${history.time_auction}"
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
                        context, R.color.black
                    )
                ) // Adjust the color as needed
                // Add padding inside the TextView for better text alignment
                setPadding(16, 16, 16, 16)
            }
            // Add the new TextView to the parent LinearLayout
            itemListLayout.addView(newItem)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
