package com.anago.linedownloader.models

/*
    静止: static
    アニメーション: animation
    アニメーションと音声: animation_sound
    飛び出す: popup
 */
data class StampItem(
    val type: String,
    val id: String,
    val imageUrl: String,
    val animationUrl: String
)