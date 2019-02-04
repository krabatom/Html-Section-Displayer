package cz.slevomat.dominika.htmldisplayer.ProductDisplayer.RecyclerViewItems

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.bumptech.glide.Glide
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import cz.slevomat.dominika.htmldisplayer.R
import kotlinx.android.synthetic.main.video_item.*

/**
 * Groupie item for a video item
 */
class RecyclerViewVideo (private val videoId: String): Item() {
    private val TAG: String = RecyclerViewVideo::class.java.simpleName
    private val URL_VIDEO_PREFIX = "https://www.youtube.com/watch?v="
    private val URL_VIDEO_THUMBNAIL = "http://img.youtube.com/vi/%s/0.jpg"

    override fun getLayout() = R.layout.video_item

    override fun bind(viewHolder: ViewHolder, position: Int) {
        displayVideoThumbnail(videoId, viewHolder)
        viewHolder.video_view_item.setOnClickListener {
            launchYoutubeId(viewHolder.video_view_item.context, videoId) }
    }

    private fun launchYoutubeId(context: Context, youtubeId: String) {
        val url = URL_VIDEO_PREFIX + youtubeId
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.putExtra("force_fullscreen", true)
        intent.putExtra("finish_on_ended", true)
        context.startActivity(intent)
    }

    private fun displayVideoThumbnail(videoId: String, viewHolder: ViewHolder){
        val imgURL = String.format(URL_VIDEO_THUMBNAIL, videoId)
        Glide.with(viewHolder.video_view_item.context).load(imgURL).into(viewHolder.video_view_item)
    }
}