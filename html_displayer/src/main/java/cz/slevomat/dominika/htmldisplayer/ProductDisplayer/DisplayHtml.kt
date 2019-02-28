package cz.slevomat.dominika.htmldisplayer.ProductDisplayer

import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.util.Log
import cz.slevomat.dominika.htmldisplayer.Models.BaseItem
import org.jsoup.Jsoup
import kotlin.collections.ArrayList
import kotlinx.coroutines.*

class DisplayHtml{
    private val TAG: String = DisplayHtml::class.java.simpleName

    val dataItems: ArrayList<BaseItem> = arrayListOf()
    private var liLevel = 0 //controlling <li> level for setting proper padding when being displayed
    private val TAG_TABLE: String = "table"
    private val TAG_LIST: String = "li"
    private val TAG_LIST_UNORDER: String = "ul"
    private val TAG_LIST_ORDER: String = "ol"
    private val TAG_BREAK: String = "br"
    private val TAG_STRONG: String = "strong"
    private val TAG_BOLD: String = "b"
    private val TAG_EMPH: String = "em"
    private val TAG_ITALIC: String = "i"
    private val TAG_HYPERLINK: String = "a"
    val decoratorArray: ArrayList<String> = arrayListOf()

    private fun isHTML(sHtml: String): Boolean{
        val regex = """<[a-z][\s\S]*>""".toRegex()
        return regex.containsMatchIn(sHtml)
    }

    /**
     * Parse html using Jsoup
     */
    private fun parseHtml(sHtml: String): org.jsoup.nodes.Document {
        val parsedString = Jsoup.parse(sHtml)
        //check if it is html
        return if (!isHTML(sHtml)){
            //sHtml is only text without html tags
            //add Html tags so it can be processed as a html string
            Jsoup.parse("<p>$sHtml</p>")
        }
        else parsedString
    }

    fun createSectionsFromHtml(sHtml: String, listener: SetDataListener)
            = GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
        dataItems.clear()
        try {
            val job = async(Dispatchers.Default) {
                htmlRecursion(
                        parseHtml(sHtml).body().childNodes(), DataType.UNKNOWN,this@DisplayHtml, "")
            }
            job.await()
            listener.setDataset(dataItems)
        } catch (e: Exception) {
            Log.e(TAG, "parsing failed", e)
        }

    }

    /**
     * Recursively go through parsed html and with processed nodes fill dataItems array based on node's tag
     */
    private fun htmlRecursion(children: MutableList<org.jsoup.nodes.Node>, dataType: DataType, instance : DisplayHtml, textSoFar : String): SpannableString {
            if (children.size > 0) {
                var spannableBuilder = SpannableStringBuilder("")
                var olCounter = 0  //specifies numerical mark before ordered list item
                for (child in children) {
                    // if this child node starts new <ul> or <ol>, increment liLevel
                    if (child.nodeName() == TAG_LIST_UNORDER || child.nodeName() == TAG_LIST_ORDER) liLevel += 1
                    if (child.nodeName() == TAG_BREAK) spannableBuilder.append("\n")

                    if (child.childNodeSize() != 0) {
                        when(child.nodeName()){
                            TAG_LIST_UNORDER -> {
                                if (!spannableBuilder.toString().isBlank() && dataType != DataType.LIST_ORDERED && dataType != DataType.LIST_UNORDERED){
                                    //there is text in spannableBuilder before <ul> so add it to dataItems
                                    //and clear spannableBuilder
                                    Displayer.addTextItem(SpannableString(spannableBuilder), instance)
                                    spannableBuilder = SpannableStringBuilder("")
                                }

                                if (dataType == DataType.LIST_UNORDERED || dataType == DataType.LIST_ORDERED){
                                    if (liLevel >= 1){
                                        spannableBuilder = Displayer.processNodeAndAdd(child, spannableBuilder, liLevel - 1, olCounter, dataType, instance)
                                    }
                                    else spannableBuilder = Displayer.processNodeAndAdd(child, spannableBuilder, liLevel, olCounter, dataType, instance)

                                }
                                spannableBuilder.append(htmlRecursion(child.childNodes(),DataType.LIST_UNORDERED, instance, spannableBuilder.toString()))
                            }
                            TAG_LIST_ORDER -> {
                                if (!spannableBuilder.toString().isBlank() && dataType != DataType.LIST_ORDERED && dataType != DataType.LIST_UNORDERED){
                                    //there is text in spannableBuilder before <ul> so add it to dataItems
                                    //and clear spannableBuilder
                                    Displayer.addTextItem(SpannableString(spannableBuilder), instance)
                                    spannableBuilder = SpannableStringBuilder("")
                                }
                                spannableBuilder.append(htmlRecursion(child.childNodes(),DataType.LIST_ORDERED, instance, spannableBuilder.toString()))
                            }

                            TAG_LIST -> spannableBuilder.append(htmlRecursion(child.childNodes(),dataType, instance, spannableBuilder.toString()))
                            TAG_TABLE -> spannableBuilder.append(Displayer.processTableItem(spannableBuilder, child.childNodes(), instance))
                            TAG_ITALIC, TAG_EMPH, TAG_STRONG, TAG_BOLD -> {
                                //text might have more tags (eg. strong and i) so to decorate text
                                //with all its tags later add tags to array
                                decoratorArray.add(child.nodeName())
                                spannableBuilder.append(htmlRecursion(child.childNodes(),DataType.UNKNOWN, instance, spannableBuilder.toString()))
                                decoratorArray.removeAt(decoratorArray.size - 1)
                            }
                            TAG_HYPERLINK -> {
                                spannableBuilder.append(Displayer.processHyperlink(child, textSoFar + spannableBuilder.toString()))
                            }
                            else -> {
                                spannableBuilder.append(htmlRecursion(child.childNodes(),DataType.UNKNOWN, instance, spannableBuilder.toString()))
                            }
                        }
                        when(child.nodeName()){
                            TAG_LIST_ORDER, TAG_LIST_UNORDER -> {
                                //when whole <ul> or <ol> section in html is processed, decrement liLevel
                                liLevel -= 1
                            }
                            TAG_LIST -> {
                                if (dataType == DataType.LIST_ORDERED) {
                                    //increment numerical mark for next ordered list item
                                    olCounter += 1
                                }
                            }
                        }
                        //html tag section is completely processed so add it as a groupie item
                        spannableBuilder = Displayer.processNodeAndAdd(child, spannableBuilder, liLevel, olCounter, dataType, instance)

                    } else {
                        //
                        spannableBuilder = SpannableStringBuilder(
                                Displayer.manageAttributes(child.attributes(), child.parentNode().nodeName(),spannableBuilder,instance, textSoFar + spannableBuilder.toString()))
                    }
                }
                return SpannableString(spannableBuilder)
            }
            else return SpannableString("")
        }


    interface SetDataListener {
        fun setDataset(dataset: ArrayList<BaseItem>)
    }
}