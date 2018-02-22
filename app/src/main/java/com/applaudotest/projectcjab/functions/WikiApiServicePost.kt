package com.applaudotest.projectcjab.functions

import com.applaudotest.projectcjab.models.ResponseLogin
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import retrofit2.http.Query

interface WikiApiServicePost {
    var urls:String
    fun WikiApiService(url:String?){
        urls=url!!
    }
  /*  @GET("api.php")
    fun hitCountCheck(@Query("action") action: String,
                       @Query("format") format: String,
                       @Query("list") list: String,
                       @Query("srsearch") srsearch: String): Observable<Model.Result>*/


    @POST("services.aspx")
    fun log(@Query("correo") correo: String,
                      @Query("clave") clave: String,
                      @Query("device_id") device_id: String): Observable<ResponseLogin>



    companion object {
        fun create(): WikiApiServicePost {

            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("http://appscla.mobilesv.com:3030/gex_dotnet_dev3/")
                    .build()

            return retrofit.create(WikiApiServicePost::class.java)
        }
    }

}