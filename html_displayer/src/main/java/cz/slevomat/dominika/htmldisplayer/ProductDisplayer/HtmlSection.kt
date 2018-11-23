package cz.slevomat.dominika.htmldisplayer.ProductDisplayer

import cz.slevomat.dominika.htmldisplayer.Models.GroupieItem
import cz.slevomat.dominika.htmldisplayer.ProductDisplayer.RecyclerViewItems.*
import com.xwray.groupie.Group
import com.xwray.groupie.Section
import java.util.*
import kotlin.collections.ArrayList

open class HtmlSection(headerItem: Group? = null) : Section(headerItem ), DisplayHtml.SetDataListener {
    private var htmlContent: String? = ""
    /**
     * Async parse and show html content in android views.
     */
    fun loadAsync(htmlContent: String?) {
        //do not reload same content
        if (this.htmlContent == htmlContent) {
            return
        }
        this.htmlContent = htmlContent
        DisplayHtml().createSectionsFromHtml(htmlContent,this)
    }

    override fun setDataset(dataset: ArrayList<GroupieItem>) {
        updateSection(dataset)
    }

    private fun updateSection(data: ArrayList<GroupieItem>){
        val groups = ArrayList<Group>()
        for (item in data){
            groups.add(createGroupieItem(item))
        }
        update(groups)
    }

    /**
     * Create section of groupie item based on it's type
     */
    private fun createGroupieItem(item: GroupieItem): Section{
        return when(item.dataType){
            DataType.TEXT -> Section(RecyclerViewText(item.textToDisplay))
            DataType.IMAGE -> Section(RecyclerViewImage(item.textUrl))
            DataType.LIST_ORDERED -> Section(RecyclerViewList(item.textToDisplay, item.liLevel))
            DataType.LIST_UNORDERED -> Section(RecyclerViewList(item.textToDisplay, item.liLevel))
            DataType.YOUTUBE -> Section(RecyclerViewVideo(item.textUrl))
            DataType.TABLE -> Section(RecyclerViewTable(item.table.rows!!))
            DataType.UNKNOWN -> throw UnknownFormatConversionException("Unknown type HTML type")
        }
    }
}