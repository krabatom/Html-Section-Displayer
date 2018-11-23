package cz.slevomat.dominika.productHtmlParser.Retrofit

import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface RetrofitClient {
    @GET(RetrofitStrings.GET)
    @Headers(RetrofitStrings.TOKEN)
    fun getProductDescription(@Query(RetrofitStrings.QUERY_ID) id: Long) : Observable<DetailProductResponse>

    companion object {
        fun create(): RetrofitClient {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(
                            RxJava2CallAdapterFactory.create())
                    .addConverterFactory(
                            GsonConverterFactory.create())
                    .baseUrl(RetrofitStrings.URL_BASE)
                    .build()

            return retrofit.create(RetrofitClient::class.java)
        }
    }
}