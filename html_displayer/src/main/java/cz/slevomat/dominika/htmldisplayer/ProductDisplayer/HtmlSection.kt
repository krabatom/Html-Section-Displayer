package cz.slevomat.dominika.htmldisplayer.ProductDisplayer

import cz.slevomat.dominika.htmldisplayer.Models.BaseItem
import cz.slevomat.dominika.htmldisplayer.ProductDisplayer.RecyclerViewItems.*
import com.xwray.groupie.Group
import com.xwray.groupie.Section
import java.util.*
import kotlin.collections.ArrayList

open class HtmlSection(headerItem: Group? = null) : Section(headerItem), DisplayHtml.SetDataListener {

    private var htmlContent: String? = ""

    /**
     * Async parse and show html content in android views
     */
    fun loadAsync(htmlContent: String?) {
        if (htmlContent == null){
            return
        }
        //do not reload same content
        if (this.htmlContent == htmlContent) {
            return
        }
        this.htmlContent = htmlContent
        DisplayHtml().createSectionsFromHtml(htmlContent, this)
    }

    override fun setDataset(dataset: ArrayList<BaseItem>) {
        updateSection(dataset)
    }

    /**
    * Create array of final groupie items from data array and display
    **/
    private fun updateSection(data: ArrayList<BaseItem>) {
        val groups = ArrayList<Group>()
        for (item in data) {
            groups.add(createGroupieItem(item))
        }
        update(groups)
    }

    /**
     * Create new section based on groupie item's type
     */
    private fun createGroupieItem(item: BaseItem): Section {
        return when (item.dataType) {
            DataType.TEXT -> Section(RecyclerViewText(item.textToDisplay))
            DataType.IMAGE -> Section(RecyclerViewImage(item.url))
            DataType.LIST_ORDERED -> Section(RecyclerViewList(item.textToDisplay, item.liLevel))
            DataType.LIST_UNORDERED -> Section(RecyclerViewList(item.textToDisplay, item.liLevel))
            DataType.YOUTUBE -> Section(RecyclerViewVideo(item.url))
            DataType.TABLE -> Section(RecyclerViewTable(item.table.rows!!))
            DataType.UNKNOWN -> throw UnknownFormatConversionException("Unknown HTML type")
        }
    }
}