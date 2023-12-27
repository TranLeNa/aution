package com.example.aution.ui.detail

import Category
import CategoryFetcher
import Item
import ItemFetcher
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.aution.R
import com.example.aution.databinding.ActivityCategoryDetailBinding
import kotlinx.coroutines.launch

class CategoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.itemGridLayout.removeAllViews()
        val categoryId = intent.getIntExtra("CATEGORY_ID", 1) // Use the correct key for category ID
        var category: Category? = null
        lifecycleScope.launch {
            category = CategoryFetcher().fetchCategoryById(categoryId)
            if (category != null) {
                Glide.with(this@CategoryDetailActivity).load(category?.image)
                    .into(binding.bannerImageView)

                val categoryTitle = findViewById<TextView>(R.id.categoryTitle)
                categoryTitle.text = category?.title
            }

            val items = ItemFetcher().fetchItemsByCategory(categoryId)
            updateItemUI(items)
        }


    }

    private fun updateItemUI(items: List<Item>) {
        items.forEach { item ->
            val itemBinding = LayoutInflater.from(this@CategoryDetailActivity)
                .inflate(R.layout.item_image_with_text, binding.itemGridLayout, false)

            val imageView = itemBinding.findViewById<ImageView>(R.id.imageView)
            val textView = itemBinding.findViewById<TextView>(R.id.titleTextView)
            val starttime = itemBinding.findViewById<TextView>(R.id.starttime)
            val endtime = itemBinding.findViewById<TextView>(R.id.endtime)

            itemBinding.setOnClickListener {
                val intent =
                    Intent(this@CategoryDetailActivity, ItemDetailActivity::class.java).apply {
                        putExtra("ITEM_ID", item.id)
                    }
                startActivity(intent)
            }

            Glide.with(this@CategoryDetailActivity).load(item.image).into(imageView)
            textView.text = item.title
            starttime.text = item.time_start.toString()
            endtime.text = item.time_end.toString()

//            val params = GridLayout.LayoutParams().apply {
//                rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
//                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
//                setMargins(8, 8, 8, 8)
//            }
//            itemBinding.layoutParams = params
            binding.itemGridLayout.addView(itemBinding)
        }
    }
}
