package cz.slevomat.dominika.htmldisplayer.ProductDisplayer.RecyclerViewItems

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.SpannableString
import android.view.Gravity
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import cz.slevomat.dominika.htmldisplayer.R
import kotlinx.android.synthetic.main.table_item.*

/**
 * Groupie item for table item
 */
class RecyclerViewTable (private val table: MutableList<MutableList<SpannableString>>): Item(){
    override fun getLayout() = R.layout.table_item

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val context = viewHolder.horizontal_scroll_table.context
        //if scroll view is not empty then clear it's content
        if (viewHolder.horizontal_scroll_table.childCount !=0)
            clearContent(viewHolder.horizontal_scroll_table)

        //create table layout inside horizontal scroll view and fill it with rows
        val tl = TableLayout(context)
        tl.apply {
            layoutParams = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT)
        }
        for (row in table) {
            val tr = TableRow(context)
            tr.apply {
                layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
                background = ColorDrawable(Color.GRAY)
            }

            //rows fill with string data
            for (cell in row) {
                val textView = TextView(context)
                textView.apply {
                    textView.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT)
                    text = cell
                    gravity = Gravity.END
                    val padding =  context.resources.getDimensionPixelSize(R.dimen.table_text_padding)
                    setPadding(padding, padding, padding, padding)
                    background = ColorDrawable(Color.WHITE)
                    (textView.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin =
                            context.resources.getDimensionPixelSize(R.dimen.table_bottom_line_thickness)

                }
                tr.addView(textView)
            }
            tl.addView(tr)
        }
        viewHolder.horizontal_scroll_table.addView(tl)
    }

    /**
     * Clear content of the scroll view
     */
    private fun clearContent(horizontalScrollView: HorizontalScrollView){
        horizontalScrollView.removeAllViews()
    }
}