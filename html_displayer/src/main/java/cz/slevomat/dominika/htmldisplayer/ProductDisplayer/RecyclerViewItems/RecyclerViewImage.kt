package cz.slevomat.dominika.htmldisplayer.ProductDisplayer.RecyclerViewItems

import com.bumptech.glide.Glide
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import cz.slevomat.dominika.htmldisplayer.R
import kotlinx.android.synthetic.main.image_item.*

/**
 * Groupie item for an image
 */
class RecyclerViewImage (private val imgURL: String) : Item() {

    override fun getLayout() = R.layout.image_item

    override fun bind(viewHolder: ViewHolder, position: Int) {
        Glide.with(viewHolder.image_view_item.context).load(imgURL).into(viewHolder.image_view_item)
    }
}