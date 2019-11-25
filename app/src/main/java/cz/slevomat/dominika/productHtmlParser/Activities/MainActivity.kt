package cz.slevomat.dominika.productHtmlParser.Activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import cz.slevomat.dominika.htmldisplayer.ProductDisplayer.HtmlSection
import cz.slevomat.dominika.productHtmlParser.HtmlExamples
import cz.slevomat.dominika.productHtmlParser.R
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

        val gAdapter = GroupAdapter<GroupieViewHolder>()
        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = gAdapter
        }
        displayFromId(1416545, gAdapter)
    }

    /**
     * Get description of the deal in html form from and create array of displayable groupie items
     * Only for testing
     * @param productId Id of the product to be displayed
     * @param gAdapter Adapter for displaying groupie sections
     */
    private fun displayFromId(productId: Long, gAdapter: GroupAdapter<GroupieViewHolder>) {
      // loadViaApi(productId, gAdapter)
       loadViaExample(gAdapter)
    }

    private fun loadViaExample(gAdapter: GroupAdapter<GroupieViewHolder>) {
        val htmlSection = HtmlSection()
        htmlSection.loadAsync(HtmlExamples.exHtml9)
        gAdapter.add(htmlSection)
    }

    @SuppressLint("CheckResult")
    private fun loadViaApi(productId: Long, gAdapter: GroupAdapter<GroupieViewHolder>) {
        val client = RetrofitClient.create()
        client.getProductDescription(productId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    val htmlSection = HtmlSection()
                     htmlSection.loadAsync(result.data?.product?.description
                           ?: "")
                    gAdapter.add(htmlSection)
                },
                { error -> Log.e(TAG, error.message ?: "") }
            )
    }
}