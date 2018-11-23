package cz.slevomat.dominika.htmldisplayer.Models

import android.text.SpannableString
import cz.slevomat.dominika.htmldisplayer.ProductDisplayer.DataType

/**
 * Base groupie item class
 */
class GroupieItem(val dataType: DataType, val textToDisplay: SpannableString,
                  val textUrl: String, val liLevel: Int, val table: TableModel)