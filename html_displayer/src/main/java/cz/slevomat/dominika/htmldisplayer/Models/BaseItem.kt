package cz.slevomat.dominika.htmldisplayer.Models

import android.text.SpannableString
import cz.slevomat.dominika.htmldisplayer.ProductDisplayer.DataType

/**
 * Base groupie item class storing data to be displayed
 * @param dataType type of data
 * @param textToDisplay decorated string
 * @param url url of image or id of video
 * @param liLevel defines padding from left based on the level of the list item
 */
internal class BaseItem(val dataType: DataType, val textToDisplay: SpannableString? = null,
               val url: String? = null, val liLevel: Int = 1, val table: TableModel? = null)