package cz.slevomat.dominika.htmldisplayer.ProductDisplayer

import android.graphics.Color
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BulletSpan
import android.util.Log
import cz.slevomat.dominika.htmldisplayer.Models.BaseItem
import cz.slevomat.dominika.htmldisplayer.Models.TableModel
import org.jsoup.nodes.Node

/**
 * Object Displayer decides how the data in spannable builder should be added as groupie items
 * to dataItems array and in which order based on attributes and tags of html nodes
**/
internal object Displayer {
    private val TAG: String = Displayer::class.java.simpleName

    private const val TAG_TEXT: String  = "#text"
    private const val TAG_IMAGE: String  = "src"
    private const val TAG_STRONG: String  = "strong"
    private const val TAG_LIST: String  = "li"
    private const val TAG_UL: String  = "ul"
    private const val TAG_PAR: String  = "p"
    private const val PREFIX_YOUTUBE_ID: String  = "[youtube id="
    private const val TAG_TABLE_HEADER: String  = "th"
    private const val TAG_ROW: String  = "tr"
    private const val TAG_TABLE_DATA: String  = "td"

    /**
     * Based on the attribute's name of the node object decide whether process text or image
     */
    fun manageAttributes(attributes: org.jsoup.nodes.Attributes, tag: String,
                         spannableBuilder: SpannableStringBuilder,
                         instance: DisplayHtml, textSoFar: String): SpannableString {
            for (attribute in attributes) {
                return when (attribute.key) {
                    TAG_TEXT -> processTextItem(spannableBuilder, textSoFar, attribute.value, tag, instance)
                    TAG_IMAGE -> processImageItem(attribute.value, spannableBuilder, instance)
                    else -> SpannableString(spannableBuilder)
                }
            }
            return SpannableString(spannableBuilder)
        }

    /**
     * Based on the node's tag create new groupie item and add it to dataItems
     */
    fun processNodeAndAdd(node: Node, spannableBuilder: SpannableStringBuilder,
                          ulLevel: Int, olLevel: Int, listType: DataType,
                          instance: DisplayHtml): SpannableStringBuilder{
        when (node.nodeName()){
            TAG_LIST -> {
                processListItem(SpannableString(spannableBuilder), ulLevel, olLevel, listType, instance)
                return SpannableStringBuilder("")
            }
            TAG_PAR,"h1","h2","h3","h4","h5","h6"-> {
                addTextItem(SpannableString(spannableBuilder), instance)
                return SpannableStringBuilder("")
            }
            TAG_UL -> {
                return if (listType == DataType.LIST_ORDERED || listType == DataType.LIST_UNORDERED){
                    processListItem(SpannableString(spannableBuilder), ulLevel, olLevel, listType, instance)
                    SpannableStringBuilder("")
                } else spannableBuilder
            }
            else -> return spannableBuilder
        }
    }

    private fun processTextItem(spannableBuilder: SpannableStringBuilder, textSoFar : String, text: String,
                                tag: String, instance: DisplayHtml): SpannableString{
        //if text contains id of a youtube video, add video groupie item
        return if (text.contains(PREFIX_YOUTUBE_ID)){
            addTextItemWithVideo(SpannableString(TextManager.adjustText(text, textSoFar)),spannableBuilder, textSoFar, tag, instance)
            SpannableString("")
        } else {
            SpannableString(spannableBuilder.append(TextManager.decorateText(text, textSoFar, tag, instance.decoratorArray)))
        }
    }

    fun processHyperlink(child: Node, textSoFar: String): SpannableString {
        var text = ""
        var link = ""
        if (child.attr("href") != null) {
            link = child.attr("href")
        }
        if (child.childNode(0) != null) {
            text = child.childNode(0).toString()
        }
        return TextManager.decorateHyperlink(text, link, textSoFar)
    }

    fun addTextItem(text: SpannableString, instance: DisplayHtml){
        if (text.toString().isNotBlank()) instance.dataItems.add(BaseItem(
                dataType = DataType.TEXT,
                textToDisplay = text
        ))
    }

    private fun processImageItem(url: String, spannableBuilder: SpannableStringBuilder,
                                 instance: DisplayHtml): SpannableString{
        // if spannable builder is not empty, add firstly the text in the builder and then add the image
        if (spannableBuilder.toString().isNotBlank()) addTextItemWithImage(spannableBuilder, url,instance)
            else addImageItem(url,instance)
        return SpannableString("")
    }

    private fun addTextItemWithImage(spannableBuilder: SpannableStringBuilder, url: String,
                                     instance: DisplayHtml){
        addTextItem(SpannableString(spannableBuilder), instance)
        addImageItem(url, instance)
    }

    private fun addImageItem(url: String, instance: DisplayHtml){
        instance.dataItems.add(BaseItem(
                dataType = DataType.IMAGE,
                url = url
        ))
    }

    private fun addTextItemWithVideo(spannableText: SpannableString, spannableBuilder: SpannableStringBuilder,
                                     textSoFar : String, tag: String, instance: DisplayHtml){
        //check whether before and after "[youtube id=..." is text and add it in correct order
        val startIndex = spannableText.indexOf(PREFIX_YOUTUBE_ID)
        // Find end index AFTER start index (postfix can appear in text multiple times without special meaning)
        val endIndex = spannableText.indexOf("\"]", startIndex = startIndex)+2

        var youtubeID = spannableText.substring(startIndex, endIndex)
        val textBeforeVideo = spannableText.subSequence(0, startIndex)
        val textAfterVideo = spannableText.subSequence(endIndex, spannableText.length)
        youtubeID = youtubeID.removePrefix(PREFIX_YOUTUBE_ID + "\"")
        youtubeID = youtubeID.removeSuffix("\"]")
        if (spannableBuilder.toString().isNotBlank()) {
            addTextItem(SpannableString(spannableBuilder.append(textBeforeVideo)), instance)
        }
        else addTextItem(SpannableString(textBeforeVideo),instance)
        addVideoItem(youtubeID, instance)
        processTextItem(spannableBuilder, textSoFar, textAfterVideo.toString(), tag, instance)
    }

    private fun addVideoItem(videoId: String, instance: DisplayHtml) {
        instance.dataItems.add(BaseItem(
                dataType = DataType.YOUTUBE,
                url = videoId
        ))
    }


    private fun addListItem(text: SpannableString, ulLevel: Int, instance: DisplayHtml){
            if (text.toString().isNotBlank()) instance.dataItems.add(BaseItem(
                    dataType = DataType.LIST_ORDERED,
                    textToDisplay = text,
                    liLevel = ulLevel
            ))
        }

    private fun addOrderedListItem(bulletBuilder: SpannableString, ulLevel: Int, olLevel: Int,
                                   instance: DisplayHtml){
        if (bulletBuilder.toString().isNotBlank()){
            val newSpanString = SpannableStringBuilder("$olLevel.  ")
            newSpanString.append(bulletBuilder)
            addListItem(SpannableString(newSpanString), ulLevel, instance)
        }
    }

    private fun addUnorderedListItem(bulletBuilder: SpannableString, ulLevel: Int, instance: DisplayHtml){
        if (bulletBuilder.toString().isNotBlank()){
            val bulletSpan = BulletSpan(15, Color.BLACK)
            bulletBuilder.setSpan(bulletSpan, 0 , bulletBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE )
            addListItem(bulletBuilder, ulLevel, instance)
        }
    }

    private fun processListItem(bulletBuilder: SpannableString, ulLevel: Int, olLevel: Int,
                                listType: DataType, instance: DisplayHtml){
        when(listType){
            DataType.LIST_ORDERED -> addOrderedListItem(bulletBuilder, ulLevel, olLevel, instance)
            DataType.LIST_UNORDERED -> addUnorderedListItem(bulletBuilder, ulLevel, instance)
            else -> Log.e(TAG, "bad data type for list $listType")
        }
    }

    fun processTableItem(spannableBuilder: SpannableStringBuilder, children: MutableList<Node>,
                         instance: DisplayHtml): SpannableString{
        if (spannableBuilder.toString().isBlank()) {
            createTableAndAdd(children, instance)
        }
        else addTextItemWithTable(spannableBuilder, children, instance)
        return SpannableString("")
    }

    private fun processRowItem(rowElement: MutableList<Node>): MutableList<SpannableString>{
        val rowItems: MutableList<SpannableString> = arrayListOf()
        for (element in rowElement){
            when(element.nodeName()){
                TAG_TABLE_DATA -> rowItems.add(getTextFromRowItem(element, ""))
                TAG_TABLE_HEADER -> rowItems.add(getHeaderTextFromRowItem(element))
            }
        }
        return rowItems
    }

    private fun getTextFromRowItem(element: Node, tag: String): SpannableString{
        val text = SpannableStringBuilder("")
        if (element.childNodeSize() == 0){
            return SpannableString(TextManager.decorateText(element.attr(TAG_TEXT), "", tag, ArrayList(0)))
        } else for (child in element.childNodes()){
            text.append(getTextFromRowItem(child, element.nodeName()))
        }
        return SpannableString(text)
    }

    private fun getHeaderTextFromRowItem(element: Node): SpannableString{
        val text = SpannableStringBuilder("")
        if (element.childNodeSize() == 0){
            return TextManager.decorateText(element.attr(TAG_TEXT), "", TAG_STRONG, ArrayList(0))
        } else for (child in element.childNodes()){
            text.append(getHeaderTextFromRowItem(child))
        }
        return SpannableString(text)
    }

    private fun addTableItem(table: TableModel, instance: DisplayHtml){
        instance.dataItems.add(BaseItem(
                dataType = DataType.TABLE,
                table = table
        ))
    }

    private fun addTextItemWithTable(spannableBuilder: SpannableStringBuilder,
                                     children: MutableList<Node>, instance: DisplayHtml){
        addTextItem(SpannableString(spannableBuilder), instance)
        createTableAndAdd(children, instance)
    }

    private fun createTableAndAdd(children: MutableList<Node>, instance: DisplayHtml){
        val rows: MutableList<MutableList<SpannableString>> = arrayListOf()
        var numRows = 0
        for (child in children) {
            if (child.childNodeSize() > 0) {
                for (childish in child.childNodes()) {
                    when (childish.nodeName()) {
                        TAG_ROW -> {
                            numRows++
                            rows.add(processRowItem(childish.childNodes()))
                        }
                    }
                }
            }
        }
        val table = TableModel(rows)
        addTableItem(table, instance)
    }
}