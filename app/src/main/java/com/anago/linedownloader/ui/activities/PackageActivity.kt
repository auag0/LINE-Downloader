package com.anago.linedownloader.ui.activities

import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anago.linedownloader.R
import com.anago.linedownloader.models.ProductItem
import com.anago.linedownloader.ui.adapters.StampListAdapter
import com.anago.linedownloader.ui.viewmodels.PackageViewModel
import com.bumptech.glide.Glide

class PackageActivity : AppCompatActivity() {
    private val productItem by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("productItem", ProductItem::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("productItem")!! as ProductItem
        }
    }

    private val viewModel: PackageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_package)
        viewModel.fetchStamps(productItem.id)

        val icon: ImageView = findViewById(R.id.icon)
        Glide.with(this).load(productItem.imageUrl).placeholder(R.drawable.sentiment_satisfied).into(icon)

        val author: TextView = findViewById(R.id.author)
        author.text = productItem.author

        val title: TextView = findViewById(R.id.title)
        title.text = productItem.title

        val stampListAdapter = StampListAdapter(this)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 4)
        recyclerView.adapter = stampListAdapter

        viewModel.stamps.observe(this) { stamps ->
            stampListAdapter.submitList(stamps)
        }
    }
}