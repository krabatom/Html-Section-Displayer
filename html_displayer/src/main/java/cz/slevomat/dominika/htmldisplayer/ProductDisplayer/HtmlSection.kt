package cz.slevomat.dominika.htmldisplayer.ProductDisplayer

import com.xwray.groupie.Group
import com.xwray.groupie.Section

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

    /**
     * Create array of final groupie items from data array and display
     **/
     override fun setDataset(dataset: List<Section>) {
         val groups = ArrayList<Group>()
         for (item in dataset) {
             groups.add(item)
         }
         update(groups)
    }
}