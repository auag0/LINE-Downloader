package com.anago.linedownloader.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.anago.linedownloader.R
import com.anago.linedownloader.models.ProductItem
import com.anago.linedownloader.ui.activities.PackageActivity
import com.bumptech.glide.Glide

class ProductListAdapter(private val context: Context) :
    ListAdapter<ProductItem, ProductListAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<ProductItem>() {
        override fun areContentsTheSame(oldItem: ProductItem, newItem: ProductItem): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: ProductItem, newItem: ProductItem): Boolean {
            return oldItem.id == newItem.id
        }
    }) {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.image)
        val author: TextView = itemView.findViewById(R.id.author)
        val title: TextView = itemView.findViewById(R.id.title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.listitem_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = getItem(position)
        Glide.with(context).load(product.imageUrl).placeholder(R.drawable.sentiment_satisfied)
            .into(holder.icon)
        holder.author.text = product.author
        holder.title.text = product.title

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PackageActivity::class.java).apply {
                putExtra("productItem", product)
            }
            context.startActivity(intent)
        }
    }
}