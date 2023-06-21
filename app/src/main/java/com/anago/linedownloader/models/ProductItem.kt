package com.anago.linedownloader.models

import java.io.Serializable

data class ProductItem(
    val id: String,
    val author: String,
    val title: String,
    val imageUrl: String
) : Serializable