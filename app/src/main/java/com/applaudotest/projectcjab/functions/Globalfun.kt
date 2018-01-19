package com.applaudotest.projectcjab.functions

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.applaudotest.projectcjab.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.realm.Realm
import io.realm.RealmConfiguration
import android.media.AudioManager
import android.telephony.TelephonyManager






class Globalfun{
    //for laoding local resources by glide
    fun glideback(context: Context?, resourceId:Int?, imageViewResource: ImageView?){
        Glide
                .with(context!!)
                .load(resourceId)
                .into(imageViewResource!!)

    }




    /*funcion de intent*/
    fun gotoActvity(contexto: Activity?, act: Class<*>?, valor: String?,finalizar:Int?) {
        val intent = Intent(contexto, act)
        intent.putExtra("mensaje", valor)
        contexto!!.startActivity(intent)
        if(finalizar==1){
            contexto.finish()
        }

    }

    //inicializar realm
    fun initrealm(contexto: Context?,  realm: Realm?): Realm {
        var realminstance = realm
        try {
            Realm.init(contexto!!)
            val realmConfiguration = RealmConfiguration.Builder()
                    .name("bdexample.realm")
                    .schemaVersion(1)
                    .deleteRealmIfMigrationNeeded()
                    .build()

            realminstance = Realm.getInstance(realmConfiguration)
            Realm.setDefaultConfiguration(realmConfiguration)
        } catch (e: Exception) {
        }

        return realminstance!!
    }


    //background url glide
    fun backgroundurl(contexto: Context?, url: String?, img: ImageView?) {
        val requestOptions = RequestOptions()
        requestOptions.placeholder(R.drawable.placeholder)
        Glide.with(contexto!!)
                .setDefaultRequestOptions(requestOptions)
                .load(url)
                .into(img!!)
    }

    @SuppressLint("HardwareIds")
//make calls
    fun makecall(context:Activity?,phone:String?){
       if(ContextCompat.checkSelfPermission(context!!,
               Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED){

           if ((context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).line1Number == null) {
               // no phone
           }
           else{
               if(!isCallActive(context)){
                   //Open call
                   val intent = Intent(Intent.ACTION_CALL)
                   intent.data = Uri.parse("tel:" + phone)
                   context.startActivity(intent)
               }
           }


       }
    }

    fun isCallActive(context: Context): Boolean {
        val manager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return manager.mode == AudioManager.MODE_IN_CALL
    }

    //dialogo error
    fun llamardialog(Drawable: Int, Titulo: String, mensaje: String, TxtBoton: String,
                     contexto: Context) {
        val dialog = Dialog(contexto, R.style.hidetitle)
        dialog.setContentView(R.layout.dialog_popup)
        // set the custom dialog components - text, image and button
        val close: RelativeLayout = dialog.findViewById(R.id.btn)
        val btn_Popup: Button = dialog.findViewById(R.id.btn_Popup)

        val LogoHeader: ImageView = dialog.findViewById(R.id.popup)
        val textTitulo: TextView = dialog.findViewById(R.id.textTitulo)
        val textMensaje: TextView = dialog.findViewById(R.id.textMensaje)
        textTitulo.text = Titulo
        textMensaje.text = mensaje
        btn_Popup.text = TxtBoton
        dialog.setCancelable(false)
        // Close Button
        close.setOnClickListener {
            dialog.dismiss()
        }
        // Close Button
        btn_Popup.setOnClickListener {
            dialog.dismiss()
        }
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    //detect idf conecttion is usable
    fun isNetworkAvailable(contexto: Context): Boolean {
        val connectivityManager = contexto.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    //to share content url
    fun sharecontent(context:Activity?,data:String?){
        try{
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type="text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, data)
            context!!.startActivity(Intent.createChooser(shareIntent,context.getString(R.string.send_to)))
        }
        catch (e:Exception){}
    }

}