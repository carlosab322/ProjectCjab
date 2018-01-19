package com.applaudotest.projectcjab.activities
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.applaudotest.projectcjab.R
import com.applaudotest.projectcjab.fragments.Fragment_Content_Video
import com.applaudotest.projectcjab.fragments.Fragment_Header_Detail
import com.applaudotest.projectcjab.functions.Globalfun
import com.applaudotest.projectcjab.realm.JsonFeedBd
import io.realm.Realm
import io.realm.RealmResults

class DetailActivity : FragmentActivity() {
    //realm utils
    internal var realm: Realm? = null
    private var   mGlobalfuncts: Globalfun?=null
    private var mresultrealm: RealmResults<JsonFeedBd>?=null
    private var mphone :String =""
    private var mshare :String=""
    private var mid:String ="0"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        //initialize
        mGlobalfuncts = Globalfun()
    }
    //method to call the header fragment
    private fun headerport(){
        try {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.containerHeader, Fragment_Header_Detail.getInstance(this,mphone,mshare,mGlobalfuncts))
                    .addToBackStack(null)
                    .commit()
        } catch (e: Exception) {
        }
    }


    //method to call the body detail fragment
    private fun bodyport(){
        try {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.contentmultimedia, Fragment_Content_Video.getInstance(this,mresultrealm,mGlobalfuncts))
                    .addToBackStack(null)
                    .commit()
        } catch (e: Exception) {
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            //get id
            val extras = intent.extras
            mid = extras.getString("mensaje")
            //init realm
            //initialize realm
            realm = mGlobalfuncts!!.initrealm(this,realm)
            //asign values for fragment header and make consult in realm via id
            if(mid.isNotEmpty()){
                mresultrealm = realm!!.where(JsonFeedBd::class.java).equalTo("id",mid.toInt()).findAll()
                if(mresultrealm!!.size>0){
                    mphone= mresultrealm!![0]!!.phone_number
                    mshare= mresultrealm!![0]!!.tickets_url
                }
            }
            //call header frag
            headerport()
            //call body fragment
            bodyport()
        } catch (e: Exception) {
        }

    }
}
