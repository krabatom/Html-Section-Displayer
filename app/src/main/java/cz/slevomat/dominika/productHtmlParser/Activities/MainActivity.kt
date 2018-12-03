package cz.slevomat.dominika.productHtmlParser.Activities

import android.content.Context
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
        displayFromId(1356090  , gAdapter, this)
    }

    /**
     * Create and display array of displayable groupie sections from product description based on product ID
     * @param productId Id of the product to be displayed
     * @param gAdapter Adapter for displaying groupie sections
     */
    private fun displayFromId(productId: Long, gAdapter: GroupAdapter<ViewHolder>, context: Context) {
        val client = RetrofitClient.create()
        client.getProductDescription(productId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            val htmlSection = HtmlSection()
//                            HtmlSection.init(context)
                            htmlSection.loadAsync(result.data?.product?.description
                                    ?: "")
//                            htmlSection.loadAsync(HtmlExamples.exHtml15)
                            gAdapter.add(htmlSection)
                        },
                        { error -> Log.e(TAG, error.message) }
                )
    }
}