package com.anago.linedownloader.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anago.linedownloader.models.PackageItem
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
    val packageItem: MutableLiveData<PackageItem> = MutableLiveData()

    fun fetchStamps(packageId: String, isStamp: Boolean) {
        loading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val request = Request.Builder()
                .url(
                    if (isStamp)
                        "https://store.line.me/stickershop/product/$packageId"
                    else
                        "https://store.line.me/emojishop/product/$packageId"
                )
                .header("Accept-Language", Locale.getDefault().language)
                .build()

            API.okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful && response.body != null) {
                        packageItem.postValue(
                            parseResponse(response.body!!.string())
                        )
                        loading.postValue(false)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    packageItem.postValue(PackageItem("", ArrayList()))
                    loading.postValue(false)
                }
            })
        }
    }

    private fun parseResponse(response: String): PackageItem {
        val stampList: ArrayList<StampItem> = ArrayList()
        val document = Jsoup.parse(response)
        val stampElements = document.select(".FnStickerPreviewItem")
        stampElements.forEach { element ->
            val dataJson = JSONObject(element.dataset()["preview"]!!)
            stampList.add(
                StampItem(
                    id = dataJson.getString("id"),
                    imageUrl = dataJson.getString("staticUrl")
                )
            )
        }
        val descriptionElement = document.selectFirst(".mdCMN38Item01Txt")
        return PackageItem(
            description = descriptionElement?.text().toString(),
            stamps = stampList
        )
    }
}