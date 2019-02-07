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
    private const val TAG_STRONG: String = "strong"
    private const val TAG_BOLD: String = "b"
    private const val TAG_EMPH: String = "em"
    private const val TAG_ITALIC: String = "i"

    /**
     * Decorate text based on it's node tags
     */
    fun decorateText(text: String, textSoFar: String, node_tag: String, tags: ArrayList<String>): SpannableString {
        val adjText = adjustText(text, textSoFar)
        if (!chars.contains(adjText)) {
            // if text has more tags (eg. i and strong), process it separately
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
                when (node_tag) {
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
        } else return SpannableString("")
    }

    private fun startsWithLetter(string : String) : Boolean{
        return string.matches(Regex("[[[:alpha:]]0-9].*"))
    }

    private fun endsWithLetter(string : String) : Boolean{
        return string.matches(Regex(".*[[[:alpha:]]0-9%]"))
    }

    /**
     * Delete useless chars (eg. "\n" and "\t") in the text
     */
    fun adjustText(text: String, textSoFar: String): String {
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
        nText = nText.replace("\u2028", "")
        nText = nText.replace("&nbsp;", " ")

        if (endsWithLetter(textSoFar) && startsWithLetter(nText)){
            nText = " " + nText
        }

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
        val fontSizes = arrayOf(24, 22, 20, 18, 16, 15)
        val spText = SpannableString(text)
        spText.setSpan(AbsoluteSizeSpan(fontSizes[i - 1], true), 0, text.count(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spText.setSpan(StyleSpan(Typeface.BOLD), 0, text.count(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spText
    }

    fun decorateHyperlink(text: String, link: String, textSoFar: String): SpannableString {
        val string = SpannableString(adjustText(text, textSoFar))
        val clickSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                view.context.startActivity(intent)
            }
        }
        string.setSpan(clickSpan, 0, string.count(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return string
    }
}