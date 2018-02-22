package com.applaudotest.projectcjab.activities

import android.Manifest
import android.content.Context
import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
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
import io.realm.Realm
import io.realm.RealmResults
import pub.devrel.easypermissions.EasyPermissions
import java.io.ByteArrayInputStream
import android.widget.RelativeLayout
import android.widget.Toast
import com.applaudotest.projectcjab.functions.WikiApiServicePost
import com.applaudotest.projectcjab.realm.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Sort
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity(),EasyPermissions.PermissionCallbacks {
    //retrofit references
    private var mApiClient: ApiClient? = null
    private var mapiService: interretrofit? = null
    //vars for download
    private var mresponsestring:String?=""
    private var mjsonfeedArray:JsonFeed?=null
    //realm utils
    private var realm: Realm by Delegates.notNull()
    private var   mGlobalfuncts: Globalfun?=null
    private var resultrealm: RealmResults<JsonFeedBd>?=null
    //object for individual query
     var mresultrealm: RealmResults<JsonFeedBd>?=null


    private var disposable: Disposable? = null

    private val wikiApiServe by lazy {
        WikiApiServicePost.create()


    }

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


            // Open the realm for the UI thread.
            realm = Realm.getDefaultInstance()


            basicCRUD(realm)
            basicQuery(realm)
            basicLinkQuery(realm)

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
            resultrealm = realm.where(JsonFeedBd::class.java).findAll()
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

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
        realm.close() // Remember to close Realm when done.
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



    private fun basicCRUD(realm: Realm) {
        showStatus("Perform basic Create/Read/Update/Delete (CRUD) operations...")

        // All writes must be wrapped in a transaction to facilitate safe multi threading
        realm.executeTransaction {
            // Add a person
            val person = realm.createObject<Person>(0)
            person.name = "Young Person"
            person.age = 14
        }

        // Find the first person (no query conditions) and read a field
        val person = realm.where<Person>().findFirst()!!
        showStatus(person.name + ": " + person.age)

        // Update person in a transaction
        realm.executeTransaction {
            person.name = "Senior Person"
            person.age = 99
            showStatus(person.name + " got older: " + person.age)
        }
    }

    private fun basicQuery(realm: Realm) {
        showStatus("\nPerforming basic Query operation...")
        showStatus("Number of persons: ${realm.where<Person>().count()}")

        val ageCriteria = 99
        val results = realm.where<Person>().equalTo("age", ageCriteria).findAll()

        showStatus("Size of result set: " + results.size)
    }

    private fun basicLinkQuery(realm: Realm) {
        showStatus("\nPerforming basic Link Query operation...")
        showStatus("Number of persons: ${realm.where<Person>().count()}")

        val results = realm.where<Person>().equalTo("cats.name", "Tiger").findAll()

        showStatus("Size of result set: ${results.size}")
    }

    private fun complexReadWrite(): String {
        var status = "\nPerforming complex Read/Write operation..."

        // Open the default realm. All threads must use its own reference to the realm.
        // Those can not be transferred across threads.
        val realm = Realm.getDefaultInstance()
        try {
            // Add ten persons in one transaction
            realm.executeTransaction {
                val fido = realm.createObject<Dog>()
                fido.name = "fido"
                for (i in 1..9) {
                    val person = realm.createObject<Person>(i.toLong())
                    person.name = "Person no. $i"
                    person.age = i
                    person.dog = fido

                    // The field tempReference is annotated with @Ignore.
                    // This means setTempReference sets the Person tempReference
                    // field directly. The tempReference is NOT saved as part of
                    // the RealmObject:
                    person.tempReference = 42

                    for (j in 0..i - 1) {
                        val cat = realm.createObject<Cat>()
                        cat.name = "Cat_$j"
                        person.cats.add(cat)
                    }
                }
            }

            // Implicit read transactions allow you to access your objects
            status += "\nNumber of persons: ${realm.where<Person>().count()}"

            // Iterate over all objects
            for (person in realm.where<Person>().findAll()) {
                val dogName: String = person?.dog?.name ?: "None"

                status += "\n${person.name}: ${person.age} : $dogName : ${person.cats.size}"

                // The field tempReference is annotated with @Ignore
                // Though we initially set its value to 42, it has
                // not been saved as part of the Person RealmObject:
                check(person.tempReference == 0)
            }

            // Sorting
            val sortedPersons = realm.where<Person>().findAllSorted(Person::age.name, Sort.DESCENDING)
            status += "\nSorting ${sortedPersons.last()?.name} == ${realm.where<Person>().findAll().first()?.name}"

        } finally {
            realm.close()
        }
        return status
    }

    private fun complexQuery(): String {
        var status = "\n\nPerforming complex Query operation..."

        // Realm implements the Closable interface, therefore we can make use of Kotlin's built-in
        // extension method 'use' (pun intended).
        Realm.getDefaultInstance().use {
            // 'it' is the implicit lambda parameter of type Realm
            status += "\nNumber of persons: ${it.where<Person>().count()}"

            // Find all persons where age between 7 and 9 and name begins with "Person".
            val results = it
                    .where<Person>()
                    .between("age", 7, 9)       // Notice implicit "and" operation
                    .beginsWith("name", "Person")
                    .findAll()

            status += "\nSize of result set: ${results.size}"

        }

        return status
    }

    private fun showStatus(txt: String) {
        Log.i("TAG", txt)
    }

    private fun retrofit(){
        try{
          /*  logohome.setOnClickListener {
                *//* if (buscador.text.toString().isNotEmpty()) {
                     //beginSearch(buscador.text.toString())
                     licencia()
                 }*//*
            }*/
        }
        catch (e:Exception){}
    }


    private fun licencia() {
        ProgressBar.visibility = View.VISIBLE
        disposable = wikiApiServe.log("my","3.1.0","2")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            ProgressBar.visibility = View.GONE
                          //  txt_search_result.text = result.codigo_respuesta
                        },
                        { error ->
                            ProgressBar.visibility = View.GONE
                            Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show() }
                )
    }




    /*   @SuppressLint("SetTextI18n")
       private fun beginSearch(searchString: String) {
           ProgressBar.visibility = View.VISIBLE
           disposable = wikiApiServe.hitCountCheck("query", "json", "search", searchString)
                   .subscribeOn(Schedulers.io())
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(
                           { result ->
                               ProgressBar.visibility = View.GONE
                               txt_search_result.text = result.query.searchinfo.totalhits.toString()+"result found"
                           },
                           { error ->
                               ProgressBar.visibility = View.GONE
                               Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show() }
                   )
       }*/

}
