package com.anago.linedownloader.models

import java.io.Serializable

data class ProductItem(
    val type: String,
    val id: String,
    val author: String,
    val title: String,
    val imageUrl: String
) : Serializable