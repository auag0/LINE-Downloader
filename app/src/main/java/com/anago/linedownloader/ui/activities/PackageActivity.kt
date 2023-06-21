package com.anago.linedownloader.ui.activities

import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anago.linedownloader.R
import com.anago.linedownloader.models.ProductItem
import com.anago.linedownloader.models.StampItem
import com.anago.linedownloader.network.API.okHttpClient
import com.anago.linedownloader.ui.adapters.StampListAdapter
import com.anago.linedownloader.ui.viewmodels.PackageViewModel
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Request

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
    private var clickedStampItem: StampItem? = null

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

        val stampListAdapter = StampListAdapter(this, ::clickedStamp)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 4)
        recyclerView.adapter = stampListAdapter

        viewModel.stampItems.observe(this) { stamps ->
            stampListAdapter.submitList(stamps)
        }
    }

    private val saveImage =
        registerForActivityResult(ActivityResultContracts.CreateDocument("image/png")) { uri ->
            uri?.let { safeUri ->
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val request = Request.Builder().url(clickedStampItem!!.imageUrl).build()
                        val response = okHttpClient.newCall(request).execute()
                        if (response.isSuccessful) {
                            response.body?.use { responseBody ->
                                contentResolver.openOutputStream(safeUri, "w")?.use { out ->
                                    responseBody.byteStream().copyTo(out)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

    private fun clickedStamp(stamp: StampItem) {
        clickedStampItem = stamp
        saveImage.launch(stamp.id)
    }
}