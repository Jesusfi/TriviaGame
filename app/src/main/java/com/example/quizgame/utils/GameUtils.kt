package com.example.quizgame.utils

import androidx.core.text.HtmlCompat

object GameUtils{
    @JvmStatic
    fun formatHtmlStringForDisplay(value: String?): String {
        val temp = value ?: ""
        return HtmlCompat.fromHtml(temp, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
    }
}
