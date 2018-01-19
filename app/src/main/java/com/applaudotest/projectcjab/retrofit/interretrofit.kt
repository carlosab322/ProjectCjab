package com.applaudotest.projectcjab.retrofit

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

 interface interretrofit {
    //llamar con varios parametros en get object
    @GET("{sufijourl}")
     fun getDatosGET(
            @Path("sufijourl") sufijourl: String,
            @QueryMap options: Map<String, String>
    ): Call<JsonObject>

     //llamar con varios parametros get json array
     @GET("{sufijourl}")
     fun getDatosGETArray(
             @Path("sufijourl") sufijourl: String,
             @QueryMap options: Map<String, String>
     ): Call<JsonArray>



    //llamar con varios parametros a travez de un json en post
    @POST("{sufijo}")
     fun getDatosPOST(
            @Path("sufijo") sufijourl: String,
            @QueryMap options: Map<String, String>,
            @Body bean: JsonObject): Call<JsonObject>

    @GET("{sufijourl}")
    fun getdatosstring(
            @Path("sufijourl") sufijourl: String,
            @QueryMap options: Map<String, String>
    ): Call<String>


//obtener array
    @GET("{sufijourl}")
     fun getdataarray(
            @Path("sufijourl") sufijourl: String,
            @QueryMap options: Map<String, String>
    ): Call<JsonArray>
}