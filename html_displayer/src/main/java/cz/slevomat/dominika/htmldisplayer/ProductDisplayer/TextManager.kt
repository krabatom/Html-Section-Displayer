package cz.slevomat.dominika.htmldisplayer.ProductDisplayer

import android.graphics.Typeface
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.content.Intent
import android.net.Uri
import android.text.*
import android.text.style.ClickableSpan
import android.view.View


object TextManager {
    private val chars = arrayOf(" ", "", "\n", "\t", "\n\t")
    private val TAG_STRONG: String = "strong"
    private val TAG_BOLD: String = "b"
    private val TAG_EMPH: String = "em"
    private val TAG_ITALIC: String = "i"

    /**
     * Decorate text based on it's element tag
     */
    fun decorateText(text: String, element_tag: String, tags: ArrayList<String>): SpannableString {
        val adjText = adjustText(text)
        if (!chars.contains(adjText)) {
            if (tags.size > 1) {
                var spannableString = SpannableString(adjText)
                for (tag in tags) {
                    when (tag) {
                        TAG_STRONG, TAG_BOLD -> spannableString = decorateBold(spannableString)
                        TAG_EMPH, TAG_ITALIC -> spannableString = decorateItalic(spannableString)
                        "h1" -> return decorateHi(1, adjText)
                        "h2" -> return decorateHi(2, adjText)
                        "h3" -> return decorateHi(3, adjText)
                        "h4" -> return decorateHi(4, adjText)
                        "h5" -> return decorateHi(5, adjText)
                        "h6" -> return decorateHi(6, adjText)
                        else -> return spannableString
                    }
                }
                return spannableString
            } else {
                when (element_tag) {
                    TAG_STRONG, TAG_BOLD -> return decorateBold(adjText)
                    TAG_EMPH, TAG_ITALIC -> return decorateItalic(adjText)
                    "h1" -> return decorateHi(1, adjText)
                    "h2" -> return decorateHi(2, adjText)
                    "h3" -> return decorateHi(3, adjText)
                    "h4" -> return decorateHi(4, adjText)
                    "h5" -> return decorateHi(5, adjText)
                    "h6" -> return decorateHi(6, adjText)
                    else -> return SpannableString(adjText)
                }
            }
            return SpannableString(adjText)
        } else return SpannableString("")
    }


    /**
     * Delete "\n" and "\t" at the beginning, end and inside the text
     */
    fun adjustText(text: String): String {
        var nText = text
        while (nText.startsWith("\n") || nText.startsWith("\t")) {
            if (nText.startsWith("\n")) nText = nText.replaceFirst("\n", "")
            if (nText.startsWith("\t")) nText = nText.replaceFirst("\t", "")
        }
        while (nText.endsWith("\n") || nText.endsWith("\t")) {
            if (nText.endsWith("\n")) nText = nText.substring(0, nText.length - 1)
            if (nText.endsWith("\t")) nText = nText.substring(0, nText.length - 1)
        }
        nText = nText.replace("\n", " ")
        nText = nText.replace("\t", "")
//        if (!nText.isEmpty() || nText == " ") nText += " "
        return nText
    }

    private fun decorateBold(text: String): SpannableString {
        val spText = SpannableString(text)
        spText.setSpan(StyleSpan(Typeface.BOLD), 0, text.count(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spText
    }

    private fun decorateBold(text: SpannableString): SpannableString {
        text.setSpan(StyleSpan(Typeface.BOLD), 0, text.count(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return text
    }

    private fun decorateItalic(text: String): SpannableString {
        val spText = SpannableString(text)
        spText.setSpan(StyleSpan(Typeface.ITALIC), 0, text.count(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spText
    }

    private fun decorateItalic(text: SpannableString): SpannableString {
        text.setSpan(StyleSpan(Typeface.ITALIC), 0, text.count(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return text
    }

    private fun decorateHi(i: Int, text: String): SpannableString {
        //docasne TODO prerobit
        val fontSizes = arrayOf(24, 22, 20, 18, 16, 15)
        val spText = SpannableString(text)
        spText.setSpan(AbsoluteSizeSpan(fontSizes[i - 1], true), 0, text.count(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spText.setSpan(StyleSpan(Typeface.BOLD), 0, text.count(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spText
    }

    fun decorateHyperlink(text: String, link: String): SpannableString {
        val string = SpannableString(text)
        val clickSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                view.context.startActivity(intent)
            }

        }
        string.setSpan(clickSpan, 0, text.count(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return string
    }
}