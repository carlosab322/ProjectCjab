package com.applaudotest.projectcjab.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.applaudotest.projectcjab.R
import com.applaudotest.projectcjab.functions.Globalfun
import com.applaudotest.projectcjab.realm.JsonFeedBd
import com.devbrackets.android.exomedia.listener.OnPreparedListener
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_content_video.*
import com.google.android.gms.maps.SupportMapFragment
class Fragment_Content_Video : Fragment(), OnPreparedListener, OnMapReadyCallback,
        GoogleMap.OnMapLoadedCallback {
    val ZOOM = 16f
    private var mMap: GoogleMap? = null
    var options: MarkerOptions? = null
    var mapFragment: SupportMapFragment? = null
    internal var dest: LatLng? = null
    override fun onMapLoaded() {
        if (mMap != null) {
            //evento click del mapa
            mMap!!.setOnMapClickListener {
            }
            if(mrealmresult!!.size>0){
                if (mrealmresult!![0]!!.latitude.toDouble() != 0.0 && mrealmresult!![0]!!.longitude.toDouble() != 0.0) {
                    mMap!!.clear()

                    options!!.position(dest!!)

                    options!!.title(mrealmresult!![0]!!.team_name)
                    options!!.snippet(mrealmresult!![0]!!.address)
                    mMap!!.addMarker(options)
                    val builder = LatLngBounds.Builder()
                    builder.include(dest)
                    //actualizacion de camara si hay gps
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(dest, ZOOM)
                    mMap!!.animateCamera(cameraUpdate)
                }
            }

        }

    }

    override fun onMapReady(googlemap: GoogleMap?) {
        val conexiones = mGlobal!!.isNetworkAvailable(context!!)
        if (conexiones) {
            try {
                //crear posicion del punto
                if(mrealmresult!!.size>0){
                    dest = LatLng(mrealmresult!![0]!!.latitude.toDouble(), mrealmresult!![0]!!.longitude.toDouble())
                }

                //pass the instance
                mMap = googlemap
            } catch (e: NumberFormatException) {
                // p did not contain a valid double
            }
            if(mrealmresult!!.size>0){
                if (mrealmresult!![0]!!.latitude.toDouble() != 0.0 && mrealmresult!![0]!!.longitude.toDouble() != 0.0) {
                    //ajustes de camara del mapa
                    mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(dest, ZOOM))
                    mMap!!.animateCamera(CameraUpdateFactory.zoomTo(ZOOM), 2000, null)
                    //crear opcion de marker
                    options = MarkerOptions()
                    mMap!!.setOnMapLoadedCallback(this)
                    mMap!!.uiSettings.isMyLocationButtonEnabled = true

                }
            }

        }
    }

    private var rootView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_content_video, container, false)

        return rootView
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHeader()

        //asyncMAP y soprte de fragment
        try{

            mapFragment = childFragmentManager.findFragmentById(R.id.smaps) as SupportMapFragment?
            mapFragment!!.getMapAsync(this)
        }
        catch (e:Exception){

        }


    }
    fun setHeader() {
        try {
            //image from detail
            if(mrealmresult!!.size>0){
                mGlobal!!.backgroundurl(context!!, mrealmresult!![0]!!.img_stadium,logo)
                texttitle.text = mrealmresult!![0]!!.team_name
                textdetail.text = mrealmresult!![0]!!.description

                setupVideo()
            }


        } catch (e: Exception) {
        }
    }

    override fun onPrepared() {
        //plays the video playback as soon as it is ready
        //video_view.start()
    }
    //for video with exomedia
    private fun setupVideo() {
        try{
            // onpreparelistener
            video_view.setOnPreparedListener(this)
            //url of detail
            video_view.setVideoURI(Uri.parse(
                    mrealmresult!![0]!!.video_url))
        }
        catch (e:Exception){}


    }

    companion object {
        private var context: Context? = null
        private  var mGlobal:Globalfun?=null
        private var mrealmresult: RealmResults<JsonFeedBd>?=null
        fun getInstance(_context: Context?, _item: RealmResults<JsonFeedBd>?, mGlobalfun: Globalfun?): Fragment {
            val fragment = Fragment_Content_Video()
            try {
                context = _context
                mGlobal = mGlobalfun
                mrealmresult = _item

            } catch (e: Exception) {
            }

            return fragment
        }
    }



}