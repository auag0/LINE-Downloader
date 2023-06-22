package com.anago.linedownloader.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anago.linedownloader.R
import com.anago.linedownloader.models.ProductItem
import com.anago.linedownloader.models.StampItem
import com.anago.linedownloader.ui.adapters.StampListAdapter
import com.anago.linedownloader.ui.viewmodels.PackageViewModel
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL


class PackageActivity : AppCompatActivity() {
    private val productItem by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("productItem", ProductItem::class.java)
                ?: throw IllegalStateException("productItem is required")
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("productItem") as? ProductItem
                ?: throw IllegalStateException("productItem is required")
        }
    }

    private val viewModel: PackageViewModel by viewModels()
    private var clickedStampItem: StampItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_package)

        val loadingView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_small_loading, null, false)
        val loadingDialog = AlertDialog.Builder(this, R.style.small_loading)
            .setView(loadingView)
            .setCancelable(false)
            .create()

        viewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                loadingDialog.show()
            } else {
                loadingDialog.dismiss()
            }
        }

        val isStamp = productItem.type == "STICKER"
        val spanCount = if (isStamp) 4 else 5

        viewModel.fetchStamps(productItem.id, isStamp)

        val icon: ImageView = findViewById(R.id.icon)
        Glide.with(this)
            .load(productItem.imageUrl)
            .placeholder(R.drawable.sentiment_satisfied)
            .into(icon)

        val author: TextView = findViewById(R.id.author)
        author.text = productItem.author

        val title: TextView = findViewById(R.id.title)
        title.text = productItem.title

        val allDownloadBtn: Button = findViewById(R.id.allDownloadBtn)
        allDownloadBtn.setOnClickListener {
            saveAllImages.launch(null)
        }

        val stampListAdapter = StampListAdapter(this, spanCount) { stamp ->
            clickedStampItem = stamp
            saveImage.launch(stamp.id)
        }
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, spanCount)
        recyclerView.adapter = stampListAdapter

        val description: TextView = findViewById(R.id.description)

        viewModel.packageItem.observe(this) { packageItem ->
            stampListAdapter.submitList(packageItem.stamps)
            description.text = packageItem.description
        }
    }

    private val saveAllImages =
        registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
            uri?.let { safeUri ->
                contentResolver.takePersistableUriPermission(
                    safeUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val documentFile = DocumentFile.fromTreeUri(applicationContext, safeUri)
                        viewModel.packageItem.value?.stamps?.forEach { stampItem ->
                            val file = documentFile?.createFile("image/png", "${stampItem.id}.png")
                            file?.let { safeFile ->
                                contentResolver.openOutputStream(safeFile.uri, "w")?.use { out ->
                                    URL(stampItem.imageUrl).openStream().use { inputStream ->
                                        inputStream.copyTo(out)
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

    private val saveImage =
        registerForActivityResult(ActivityResultContracts.CreateDocument("image/png")) { uri ->
            uri?.let { safeUri ->
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        contentResolver.openOutputStream(safeUri, "w")?.use { out ->
                            URL(clickedStampItem!!.imageUrl).openStream().copyTo(out)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
}