package com.anago.linedownloader.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anago.linedownloader.models.ProductItem
import com.anago.linedownloader.network.API.okHttpClient
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.Locale

class ProductListViewModel : ViewModel() {
    val loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val productItems: MutableLiveData<ArrayList<ProductItem>> = MutableLiveData(ArrayList())

    fun searchProduct(keyword: String, isStamp: Boolean) {
        loading.postValue(true)
        val request = Request.Builder()
            .url(
                if (isStamp)
                    "https://store.line.me/api/search/sticker?query=$keyword&offset=0&limit=36&type=ALL"
                else
                    "https://store.line.me/api/search/emoji?query=$keyword&offset=0&limit=36&type=ALL"
            )
            .header("Accept-Language", Locale.getDefault().language)
            .build()
        
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful && response.body != null) {
                    val productItemList = parseResponse(response.body!!.string())
                    productItems.postValue(productItemList)
                    loading.postValue(false)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                productItems.postValue(ArrayList())
                loading.postValue(false)
            }
        })
    }

    private fun parseResponse(response: String): ArrayList<ProductItem> {
        val productItemList: ArrayList<ProductItem> = ArrayList()
        val jsonObject = JSONObject(response)
        val items = jsonObject.getJSONArray("items")
        for (i in 0 until items.length()) {
            val item = items.getJSONObject(i)
            productItemList.add(
                ProductItem(
                    type = item.getString("type"),
                    id = item.getString("id"),
                    author = item.getString("authorName"),
                    title = item.getString("title"),
                    imageUrl = item.getJSONObject("listIcon").getString("src")
                )
            )
        }
        return productItemList
    }
}