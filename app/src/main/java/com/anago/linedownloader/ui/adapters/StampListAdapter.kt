package com.anago.linedownloader.ui.adapters

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.anago.linedownloader.R
import com.anago.linedownloader.models.StampItem
import com.bumptech.glide.Glide

class StampListAdapter(
    private val context: Context,
    private val clickedStamp: (stamp: StampItem) -> Unit
) :
    ListAdapter<StampItem, StampListAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<StampItem>() {
        override fun areItemsTheSame(oldItem: StampItem, newItem: StampItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StampItem, newItem: StampItem): Boolean {
            return oldItem == newItem
        }
    }) {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.listitem_stamp, parent, false)
        val imageSize = (getScreenWidth() / 4) - 1
        view.layoutParams = ViewGroup.LayoutParams(imageSize, imageSize)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val stamp = getItem(position)
        val imageView: ImageView = holder.image
        Glide.with(context).load(stamp.imageUrl).placeholder(R.drawable.sentiment_satisfied)
            .into(imageView)

        holder.itemView.setOnClickListener {
            clickedStamp(stamp)
        }
    }

    private fun getScreenWidth(): Int {
        val windowManager = context.getSystemService(WindowManager::class.java)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowManager.currentWindowMetrics.bounds.width()
        } else {
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }
}