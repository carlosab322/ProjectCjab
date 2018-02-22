package com.applaudotest.projectcjab.functions

import com.applaudotest.projectcjab.models.LicenciaUrl
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WikiApiService {
    var urls:String
    fun WikiApiService(url:String?){
        urls=url!!
    }
  /*  @GET("api.php")
    fun hitCountCheck(@Query("action") action: String,
                       @Query("format") format: String,
                       @Query("list") list: String,
                       @Query("srsearch") srsearch: String): Observable<Model.Result>*/


    @GET("default.aspx")
    fun controllicencias(@Query("id") action: String,
                      @Query("appversion") format: String,
                      @Query("section") list: String,
                      @Query("os") srsearch: String): Observable<LicenciaUrl>



    companion object {
        fun create(): WikiApiService {

            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("http://control-de-licencias.azurewebsites.net/")
                    .build()

            return retrofit.create(WikiApiService::class.java)
        }
    }

}