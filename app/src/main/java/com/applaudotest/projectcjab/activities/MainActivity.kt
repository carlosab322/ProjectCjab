package com.applaudotest.projectcjab.activities

import android.Manifest
import android.content.Context
import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import com.applaudotest.projectcjab.R
import com.applaudotest.projectcjab.fragments.Fragment_Home
import com.applaudotest.projectcjab.models.JsonFeed
import com.applaudotest.projectcjab.retrofit.ApiClient
import com.applaudotest.projectcjab.retrofit.interretrofit
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonArray
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap
import com.applaudotest.projectcjab.adapter.RecyclerAdapter
import com.applaudotest.projectcjab.fragments.Fragment_Content_Video
import com.applaudotest.projectcjab.functions.Globalfun
import com.applaudotest.projectcjab.realm.JsonFeedBd
import com.applaudotest.projectcjab.realm.RealmImporter
import io.realm.Realm
import io.realm.RealmResults
import pub.devrel.easypermissions.EasyPermissions
import java.io.ByteArrayInputStream
import android.widget.RelativeLayout



class MainActivity : AppCompatActivity(),EasyPermissions.PermissionCallbacks {
    //retrofit references
    private var mApiClient: ApiClient? = null
    private var mapiService: interretrofit? = null
    //vars for download
    private var mresponsestring:String?=""
    private var mjsonfeedArray:JsonFeed?=null
    //realm utils
    internal var realm: Realm? = null
    private var   mGlobalfuncts: Globalfun?=null
    private var resultrealm: RealmResults<JsonFeedBd>?=null
    //object for individual query
     var mresultrealm: RealmResults<JsonFeedBd>?=null
    //permissions
      var firstime:Boolean = true
    private val mPERMISOS_APP = 124
    var mContext:Context = this
    //for detection in landscape
    var mislandscape :Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try{
            //initial retrofit
            mGlobalfuncts = Globalfun()
            mApiClient = ApiClient()
            mApiClient!!.url(getString(R.string.resourceurl))
            mapiService = mApiClient!!.getClient().create(interretrofit::class.java)
            //initialize realm
            realm = mGlobalfuncts!!.initrealm(this,realm)
            //call header portrait
            headerport()

            //call onrefresh
            onrefresh()
            //call permissions android 6 and above
            callpermissions()
        }
        catch (e:Exception){}
    }

    //funcion para consulta de realm
    private fun realmbd(){
        try{
            //consult realmwith results
            resultrealm = realm!!.where(JsonFeedBd::class.java).findAll()
            //validate if not empty
            if(resultrealm!!.size>0){
                textempty.visibility = View.GONE
                loadlistview()
            }
            else{
                textempty.visibility = View.VISIBLE
            }
        }
        catch (e:Exception){

        }
    }
    override fun onResume() {
        super.onResume()
        try {
            //call download json feed
            if(mGlobalfuncts!!.isNetworkAvailable(this)){
                textempty.visibility = View.GONE
                Download_Json()
            }
            else{
                //consult bd if data is available
                realmbd()
                //function to call dialog
                mGlobalfuncts!!.llamardialog(R.drawable.androidimage,getString(R.string.title),getString(R.string.errorconect),getString(R.string.aceptar),
                        this)
            }

        } catch (e: Exception) {
        }

    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        if (newConfig != null) {
            // Checks the orientation of the screen
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mislandscape=true
                //define dimension for list
                val paramscontent = RelativeLayout.LayoutParams(resources.getDimension(R.dimen._245sdp).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
                paramscontent.addRule(RelativeLayout.BELOW,R.id.containerHeader)
                simpleSwipeRefreshLayout.layoutParams = paramscontent
                headerport()
                //call position 1 for list in fragment
                realm_one(1)
                if(mresultrealm!!.size>0){
                    bodyport()
                }
                loadlistview()

            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
                mislandscape=false
                //define dimension for list
                val paramscontent = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                paramscontent.addRule(RelativeLayout.BELOW,R.id.containerHeader)
                simpleSwipeRefreshLayout.layoutParams = paramscontent
                headerport()
                realm_one(1)
                if(mresultrealm!!.size>0){
                    bodyport()
                }
            }

        }

    }
        //to call download of json_feed via retrofit
    private  fun Download_Json(){
            try {
                ProgressBar.visibility = View.VISIBLE
                //parameter of url
                val sufijo = getString(R.string.sufix)
                val params = HashMap<String, String>()
                val callexample = mapiService!!.getDatosGETArray(sufijo, params)
                callexample.enqueue(object : Callback<JsonArray> {
                    override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                        if (response.isSuccessful) {
                            try {
                                ProgressBar.visibility = View.GONE
                                //obtain json array
                                mresponsestring = response.body().toString()
                                //asign var string to convert as an object
                                val responsestringmod = "{\"registros\":$mresponsestring}"
                                //convert to object
                                try{
                                    mjsonfeedArray = Gson().fromJson<JsonFeed>(responsestringmod, JsonFeed::class.java)
                                    //save data to realm
                                    val mdata = ByteArrayInputStream(mresponsestring!!.toByteArray())
                                    val mimportador = RealmImporter(mdata,realm!!)
                                    mimportador.importfromjsonload()
                                    //consult realmwith results
                                   realmbd()
                                }
                                catch (e:Exception){
                                }
                            } catch (e: Exception) {
                            }

                        } else {
                            ProgressBar.visibility = View.GONE
                            //consult bd if data is available
                          realmbd()
                            println("failed download" + response.errorBody())
                            //function to call dialog
                            mGlobalfuncts!!.llamardialog(R.drawable.noconecta,getString(R.string.title),getString(R.string.errorconect1),getString(R.string.aceptar),
                                    mContext)
                        }
                    }

                    override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                        ProgressBar.visibility = View.GONE
                        //consult bd if data is available
                       realmbd()
                        //function to call dialog
                        mGlobalfuncts!!.llamardialog(R.drawable.noconecta,getString(R.string.title),getString(R.string.errorconect2),getString(R.string.aceptar),
                                mContext)

                    }
                })

            } catch (e: Exception) {
            }

    }

    //method to call an individual object realm
     fun realm_one(position:Int){
        try{
            mresultrealm = realm!!.where(JsonFeedBd::class.java).equalTo("id",position).findAll()
        }
        catch (e:Exception){}

    }

    //method to call the body detail fragment
     fun bodyport(){
        try {

            supportFragmentManager.beginTransaction()
                    .replace(R.id.containerfragmentvid, Fragment_Content_Video.getInstance(this,mresultrealm,mGlobalfuncts))
                    .addToBackStack(null)
                    .commit()
        } catch (e: Exception) {
        }
    }
            //method to call the header fragment
    private fun headerport(){
            try {
                if(mresultrealm!=null){
                    if(mresultrealm!!.size>0){
                        supportFragmentManager.beginTransaction()
                                .replace(R.id.containerHeader, Fragment_Home.getInstance(this,mresultrealm!![0]!!.phone_number,mresultrealm!![0]!!.tickets_url,mGlobalfuncts))
                                .addToBackStack(null)
                                .commit()
                    }
                    else{
                        supportFragmentManager.beginTransaction()
                                .replace(R.id.containerHeader, Fragment_Home.getInstance(this,"","",mGlobalfuncts))
                                .addToBackStack(null)
                                .commit()
                    }
                }
                else{
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.containerHeader, Fragment_Home.getInstance(this,"","",mGlobalfuncts))
                            .addToBackStack(null)
                            .commit()
                }


            } catch (e: Exception) {
            }
    }
    //List load method with data
    private fun loadlistview(){
        //reciclerview

        listfeed.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        linearLayoutManager.findFirstCompletelyVisibleItemPosition()
        listfeed.layoutManager = linearLayoutManager
        //adapter
        val adapter = RecyclerAdapter(resultrealm!!, this,mGlobalfuncts,firstime)
        listfeed.adapter = adapter
        adapter.notifyDataSetChanged()

    }
    //to refresh on swipe the list
    private  fun onrefresh(){
        // implement  event on SwipeRefreshLayout
        simpleSwipeRefreshLayout.setOnRefreshListener({
            // implement Handler to wait for 3 seconds and then update UI
            Handler().postDelayed({
                simpleSwipeRefreshLayout.isRefreshing = false
               Download_Json()
            }, 3000)
        })
    }
    //memory fun glide
    override fun onLowMemory() {
        super.onLowMemory()
        Glide.get(this).clearMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Glide.get(this).trimMemory(level)
    }

    ///permissions android 6.0 and above
    private fun callpermissions() {
        val perms = arrayOf( Manifest.permission.CALL_PHONE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_PHONE_STATE)
        if (EasyPermissions.hasPermissions(this, *perms)) {
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(this, "You must have the permission to run properly the app",
                    mPERMISOS_APP, *perms)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
    }
    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
    }

}
