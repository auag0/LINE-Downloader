package com.anago.linedownloader.ui.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anago.linedownloader.R
import com.anago.linedownloader.ui.adapters.ProductListAdapter
import com.anago.linedownloader.ui.viewmodels.ProductListViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class ProductListActivity : AppCompatActivity() {
    private val viewModel: ProductListViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        val loadingView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_small_loading, null, false)
        val loadingDialog = AlertDialog.Builder(this, R.style.small_loading)
            .setView(loadingView)
            .setCancelable(false)
            .create()

        val productListAdapter = ProductListAdapter(this)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = productListAdapter

        viewModel.productItems.observe(this) { productItems ->
            productListAdapter.submitList(productItems)
        }

        viewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                loadingDialog.show()
            } else {
                loadingDialog.dismiss()
            }
        }

        val isStamp: Chip = findViewById(R.id.stamp)

        val keywordInput: EditText = findViewById(R.id.keyword)
        keywordInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val text = keywordInput.text.toString()
                if (text.isNotBlank()) {
                    viewModel.searchProduct(text, isStamp.isChecked)
                    return@setOnEditorActionListener true
                }
            }
            false
        }

        val group: ChipGroup = findViewById(R.id.group)
        group.setOnCheckedStateChangeListener { _, _ ->
            val text = keywordInput.text.toString()
            if (text.isNotBlank()) {
                viewModel.searchProduct(text, isStamp.isChecked)
            }
        }
    }
}