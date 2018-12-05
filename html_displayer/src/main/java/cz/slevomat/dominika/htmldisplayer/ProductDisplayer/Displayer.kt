package cz.slevomat.dominika.htmldisplayer.ProductDisplayer

import android.graphics.Color
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BulletSpan
import android.util.Log
import cz.slevomat.dominika.htmldisplayer.Models.GroupieItem
import cz.slevomat.dominika.htmldisplayer.Models.TableModel
import org.jsoup.nodes.Node
import org.w3c.dom.Text

object Displayer {
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
     * Based on the attribute's name of the node object from html decide whether start processing
     * text or image, or return inputted spannable string
     */
    fun manageAttributes(attributes: org.jsoup.nodes.Attributes, tag: String,
                         spannableBuilder: SpannableStringBuilder,
                         instance: DisplayHtml): SpannableString {
            for (attribute in attributes) {
                when (attribute.key) {
                    TAG_TEXT -> return processText(spannableBuilder, attribute.value, tag, instance)
                    TAG_IMAGE -> return processImage(attribute.value, spannableBuilder, instance)
                    else -> return SpannableString(spannableBuilder)
                }
            }
            return SpannableString(spannableBuilder)
        }

    /**
     * Based on the element's tag add item to groupieItems
     */
    fun processElementAndAdd(element: org.jsoup.nodes.Node, spannableBuilder: SpannableStringBuilder,
                             ulLevel: Int, olLevel: Int, listType: DataType,
                             instance: DisplayHtml): SpannableStringBuilder{
        when (element.nodeName()){
            TAG_LIST -> {
                processList(SpannableString(spannableBuilder), ulLevel, olLevel, listType, instance)
                return SpannableStringBuilder("")
            }
            TAG_PAR,"h1","h2","h3","h4","h5","h6"-> {
                addTextItem(SpannableString(spannableBuilder), instance)
                return SpannableStringBuilder("")
            }
            TAG_UL -> {
                if (listType == DataType.LIST_ORDERED || listType == DataType.LIST_UNORDERED){
                    processList(SpannableString(TextManager.adjustText(spannableBuilder.toString())), ulLevel, olLevel, listType, instance)
                    return SpannableStringBuilder("")
                }
                else return spannableBuilder
            }
            else -> return spannableBuilder
        }
    }

    private fun processText(spannableBuilder: SpannableStringBuilder, text: String,
                            tag: String, instance: DisplayHtml): SpannableString{
        //check if youtube id is contained in the text
        if (text.contains(PREFIX_YOUTUBE_ID)){
            addTextItemWithVideo(SpannableString(TextManager.adjustText(text)),spannableBuilder, instance)
            return SpannableString("")
        }
        else {
            return SpannableString(spannableBuilder.append(TextManager.decorateText(text, tag, instance.decoratorArray)))
        }
    }

    fun processHyperlink(child: org.jsoup.nodes.Node, spannableBuilder: SpannableStringBuilder): SpannableString {
        var text = ""
        var link = ""
        if (child.attr("href") != null) {
            link = child.attr("href")
        }
        if (child.childNode(0) != null) {
            text = child.childNode(0).toString()
        }
        return TextManager.decorateHyperlink(text, link)
    }

    private fun addTextItem(text: SpannableString, instance: DisplayHtml){
        if (text.toString().isNotBlank()) instance.groupieItems.add(GroupieItem(
                DataType.TEXT,
                text,
                "",
                1,
                TableModel(null)
        ))
    }

    private fun processImage(url: String, spannableBuilder: SpannableStringBuilder,
                             instance: DisplayHtml): SpannableString{
        // if spannable builder is not blank then add firstly the text in the builder and then add the image
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
        instance.groupieItems.add(GroupieItem(
                DataType.IMAGE,
                SpannableString(""),
                url,
                1,
                TableModel(null)
        ))
    }

    private fun addTextItemWithVideo(text: SpannableString, spannableBuilder: SpannableStringBuilder,
                                     instance: DisplayHtml){
        //check whether before and after "[youtube id=..." is text and add it in correct order
        var youtubeID = text.substring(text.indexOf(PREFIX_YOUTUBE_ID), text.indexOf("\"]")+2)
        val substrBeforeVideo = text.subSequence(0, text.indexOf(PREFIX_YOUTUBE_ID))
        val substrAfterVideo = text.subSequence(text.indexOf("\"]")+2, text.length)
        youtubeID = youtubeID.removePrefix(PREFIX_YOUTUBE_ID + "\"")
        youtubeID = youtubeID.removeSuffix("\"]")
        if (spannableBuilder.toString().isNotBlank()) {
            addTextItem(SpannableString(spannableBuilder.append(substrBeforeVideo)), instance)
        }
        else addTextItem(SpannableString(substrBeforeVideo),instance)
        addVideoItem(youtubeID, instance)
        addTextItem(SpannableString(substrAfterVideo),instance)
    }

    private fun addVideoItem(videoId: String, instance: DisplayHtml) {
        instance.groupieItems.add(GroupieItem(
                DataType.YOUTUBE,
                SpannableString(""),
                videoId,
                1,
                TableModel(null)
        ))
    }


    private fun addListItem(text: SpannableString, ulLevel: Int, instance: DisplayHtml){
            if (text.toString().isNotBlank()) instance.groupieItems.add(GroupieItem(
                    DataType.LIST_ORDERED,
                    text,
                    "",
                    ulLevel,
                    TableModel(null)
            ))
        }

    private fun addOrderedListItem(bulletBuilder: SpannableString, ulLevel: Int, olLevel: Int,
                                   instance: DisplayHtml){
        if (bulletBuilder.toString().isNotBlank()){
            val newSpanString = SpannableStringBuilder(olLevel.toString() + ".  ")
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

    private fun processList(bulletBuilder: SpannableString, ulLevel: Int, olLevel: Int,
                            listType: DataType, instance: DisplayHtml){
        when(listType){
            DataType.LIST_ORDERED -> addOrderedListItem(bulletBuilder, ulLevel, olLevel, instance)
            DataType.LIST_UNORDERED -> addUnorderedListItem(bulletBuilder, ulLevel, instance)
            else -> Log.i(TAG, "Error in type in processList()")
        }
    }

    /**
     * From list of children create table and add it to groupieItem
     */
    fun processTable(spannableBuilder: SpannableStringBuilder, children: MutableList<org.jsoup.nodes.Node>,
                     instance: DisplayHtml): SpannableString{
        if (spannableBuilder.toString().isBlank()) {
            createTableAndAdd(children, instance)
        }
        else addTextItemWithTable(spannableBuilder, children, instance)
        return SpannableString("")
    }

    private fun processRow(rowElement: MutableList<org.jsoup.nodes.Node>): MutableList<SpannableString>{
        val rowItems: MutableList<SpannableString> = arrayListOf()
        for (element in rowElement){
            when(element.nodeName()){
                TAG_TABLE_DATA -> rowItems.add(getTextFromRowItem(element, ""))
                TAG_TABLE_HEADER -> rowItems.add(getHeaderTextFromRowItem(element))
                else -> Log.i(TAG, "Error in type in processRow()")
            }
        }
        return rowItems
    }

    private fun getTextFromRowItem(element: org.jsoup.nodes.Node, tag: String): SpannableString{
        var text = SpannableStringBuilder("")
        if (element.childNodeSize() == 0){
            return SpannableString(TextManager.decorateText(element.attr(TAG_TEXT), tag, ArrayList(0)))
        } else for (child in element.childNodes()){
            text.append(getTextFromRowItem(child, element.nodeName()))
        }
        return SpannableString(text)
    }

    private fun getHeaderTextFromRowItem(element: org.jsoup.nodes.Node): SpannableString{
        val text = SpannableStringBuilder("")
        if (element.childNodeSize() == 0){
            return TextManager.decorateText(element.attr(TAG_TEXT), TAG_STRONG, ArrayList(0))
        } else for (child in element.childNodes()){
            text.append(getHeaderTextFromRowItem(child))
        }
        return SpannableString(text)
    }

    private fun addTableItem(table: TableModel, instance: DisplayHtml){
        instance.groupieItems.add(GroupieItem(
                DataType.TABLE,
                SpannableString(""),
                "",
                1,
                table
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
                            rows.add(processRow(childish.childNodes()))
                        }
                        else -> Log.i(TAG, "Error in type in processList()")
                    }
                }
            }
        }
        val table = TableModel(rows)
        addTableItem(table, instance)
    }
}