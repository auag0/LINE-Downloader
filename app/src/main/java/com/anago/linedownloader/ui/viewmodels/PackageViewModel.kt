package com.anago.linedownloader.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val stamps: MutableLiveData<List<String>> = MutableLiveData()

    fun fetchStamps(packageId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://store.line.me/stickershop/product/$packageId")
                .header("Accept-Language", Locale.getDefault().language)
                .build()
            API.okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful && response.body != null) {
                        val stampList: ArrayList<String> = ArrayList()
                        val html = response.body!!.string()
                        val document = Jsoup.parse(html)
                        val elements = document.select(".FnStickerPreviewItem")
                        elements.forEach { element ->
                            val dataJson = JSONObject(element.dataset()["preview"]!!)
                            stampList.add(dataJson.getString("staticUrl"))
                        }
                        stamps.postValue(stampList)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }
            })
        }
    }
}