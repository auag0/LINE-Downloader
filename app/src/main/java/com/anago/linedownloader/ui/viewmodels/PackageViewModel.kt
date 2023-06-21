package com.anago.linedownloader.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anago.linedownloader.models.StampItem
import com.anago.linedownloader.network.API
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.IOException
import java.util.Locale

class PackageViewModel : ViewModel() {
    val loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val stampItems: MutableLiveData<List<StampItem>> = MutableLiveData()

    fun fetchStamps(packageId: String) {
        loading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://store.line.me/stickershop/product/$packageId")
                .header("Accept-Language", Locale.getDefault().language)
                .build()

            API.okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful && response.body != null) {
                        val stampList = parseResponse(response.body!!.string())
                        stampItems.postValue(stampList)
                        loading.postValue(false)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    stampItems.postValue(ArrayList())
                    loading.postValue(false)
                }
            })
        }
    }

    private fun parseResponse(response: String): ArrayList<StampItem> {
        val stampList: ArrayList<StampItem> = ArrayList()
        val document = Jsoup.parse(response)
        val elements = document.select(".FnStickerPreviewItem")
        elements.forEach { element ->
            val dataJson = JSONObject(element.dataset()["preview"]!!)
            stampList.add(
                StampItem(
                    id = dataJson.getString("id"),
                    imageUrl = dataJson.getString("staticUrl")
                )
            )
        }
        return stampList
    }
}