package cz.slevomat.dominika.htmldisplayer.ProductDisplayer.RecyclerViewItems

import android.text.SpannableString
import android.text.method.LinkMovementMethod
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import cz.slevomat.dominika.htmldisplayer.R
import kotlinx.android.synthetic.main.text_item.*

/**
 * Groupie item for text item
 */
class RecyclerViewText (private val text: SpannableString) : Item() {

    override fun getLayout() = R.layout.text_item

    override fun bind(viewHolder: ViewHolder, position: Int) {
//        viewHolder.text_view_item.movementMethod = LinkMovementMethod.getInstance()
        viewHolder.text_view_item.text = text
    }
}
