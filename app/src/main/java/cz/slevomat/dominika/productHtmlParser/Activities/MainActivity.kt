package cz.slevomat.dominika.productHtmlParser.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import cz.slevomat.dominika.productHtmlParser.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import cz.slevomat.dominika.htmldisplayer.ProductDisplayer.HtmlSection
import cz.slevomat.dominika.productHtmlParser.HtmlExamples
import cz.slevomat.dominika.productHtmlParser.Retrofit.RetrofitClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

/*
 * For testing
 */
class MainActivity : AppCompatActivity() {
    private val TAG: String = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gAdapter = GroupAdapter<ViewHolder>()
        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = gAdapter
        }
        displayFromId(1380604, gAdapter)
    }

    /**
     * Get description of the deal in html form from and create array of displayable groupie items
     * Only for testing
     * @param productId Id of the product to be displayed
     * @param gAdapter Adapter for displaying groupie sections
     */
    private fun displayFromId(productId: Long, gAdapter: GroupAdapter<ViewHolder>) {
        val client = RetrofitClient.create()
        client.getProductDescription(productId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            val htmlSection = HtmlSection()
                            htmlSection.loadAsync(result.data?.product?.description
                                    ?: "")
//                            htmlSection.loadAsync(HtmlExamples.exHtml)
                            gAdapter.add(htmlSection)
                        },
                        { error -> Log.e(TAG, error.message) }
                )
    }
}