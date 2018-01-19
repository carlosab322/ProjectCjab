package com.applaudotest.projectcjab.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.CertificateException
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class ApiClient {
    var BASE_URL = ""
    fun url(url: String) {
        BASE_URL = url
    }

    private var retrofit: Retrofit? = null

    fun getClient(): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getUnsafeOkHttpClient())
                    .build()
        }
        return retrofit!!
    }

    private fun getUnsafeOkHttpClient(): OkHttpClient {
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                    return arrayOf()
                }
            })

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory

            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory)
            builder.readTimeout(900000, TimeUnit.MILLISECONDS)
            builder.writeTimeout(900000, TimeUnit.MILLISECONDS)
            builder.hostnameVerifier { hostname, session -> true }

            val okHttpClient = builder.build()
            builder.readTimeout(900000, TimeUnit.MILLISECONDS)
            builder.writeTimeout(900000, TimeUnit.MILLISECONDS)
            return okHttpClient
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }


}
