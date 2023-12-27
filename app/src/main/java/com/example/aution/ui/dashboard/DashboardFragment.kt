package com.example.aution.ui.dashboard

import Category
import CategoryFetcher
import Item
import ItemFetcher
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.aution.Config
import com.example.aution.R
import com.example.aution.databinding.FragmentDashboardBinding
import com.example.aution.ui.detail.CategoryDetailActivity
import com.example.aution.ui.detail.ItemDetailActivity
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val imageUrl = Config.BANNER_DASHBOARD_IMAGE
        Glide.with(this).load(imageUrl).into(binding.bannerImageView)

        binding.topRowGridLayout.removeAllViews()
        binding.itemGridLayout.removeAllViews()
        lifecycleScope.launch {
            val categories = CategoryFetcher().fetchCategories()
            updateCategoryUI(categories)
            val items = ItemFetcher().fetchItems()
            updateItemUI(items)
        }
//
//        val textView: TextView = binding.textDashboard
//        dashboardViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    private fun updateCategoryUI(categories: List<Category>) {
        if (!isAdded) return  // Check if the fragment is still attached

        val localBinding = _binding ?: return  // Safely get the binding

        localBinding.topRowGridLayout.removeAllViews()
        categories.forEach { categoryObject ->
            val imageView = ImageView(context).apply {
                // Set LayoutParams to ensure each image takes equal space
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 300 // Or any other fixed size or use WRAP_CONTENT
                    rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f) // 1f is the weight
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f) // 1f is the weight
                    setMargins(8, 8, 8, 8) // You can adjust margins as needed
                }
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

            imageView.setOnClickListener {
                // Create an intent to start the DetailActivity
                val intent = Intent(context, CategoryDetailActivity::class.java).apply {
                    putExtra("CATEGORY_ID", categoryObject.id)
                }
                startActivity(intent)

            }

            // Load the image using Glide
            Glide.with(this).load(categoryObject.image).into(imageView)

            // Add the ImageView to the GridLayout

            binding.topRowGridLayout.addView(imageView)
        }
    }

    private fun updateItemUI(items: List<Item>) {
        if (!isAdded) return  // Check if the fragment is still attached

        val localBinding = _binding ?: return  // Safely get the binding
        items.forEach { item ->
            // Inflate the custom layout for each item
            val itemBinding = LayoutInflater.from(context)
                .inflate(R.layout.item_image_with_text, binding.itemGridLayout, false)
            val imageView = itemBinding.findViewById<ImageView>(R.id.imageView)
            val textView = itemBinding.findViewById<TextView>(R.id.titleTextView)
            val starttime = itemBinding.findViewById<TextView>(R.id.starttime)
            val endtime = itemBinding.findViewById<TextView>(R.id.endtime)

            itemBinding.setOnClickListener {
                // Create an intent to start the DetailActivity
                val intent = Intent(context, ItemDetailActivity::class.java).apply {
                    putExtra("ITEM_ID", item.id)
                }
                startActivity(intent)

            }

            // Load the image using Glide
            Glide.with(this).load(item.image).into(imageView)

            // Set any text you want to display over the image
            textView.text = item.title
            starttime.text = item.time_start.toString()
            endtime.text = item.time_start.toString()

//            // Define layout parameters for adding to GridLayout
//            val params = GridLayout.LayoutParams().apply {
//                rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f) // 1f is the weight
//                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f) // 1f is the weight
//                setMargins(8, 8, 8, 8)
//            }
//            itemBinding.layoutParams = params

            // Add the inflated layout to the GridLayout
            binding.itemGridLayout.addView(itemBinding)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}