package com.applaudotest.projectcjab.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.applaudotest.projectcjab.R
import android.view.animation.DecelerateInterpolator
import android.animation.ObjectAnimator
import android.os.Handler
import android.view.View
import com.applaudotest.projectcjab.functions.Fonts
import com.applaudotest.projectcjab.functions.Globalfun
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_splash.*
import android.view.animation.AnimationUtils
import android.widget.ImageView
class SplashActivity : AppCompatActivity() {
      private var   mGlobalfuncts:Globalfun?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //initialize
        try{
            mGlobalfuncts = Globalfun()

            //set text font
            textsplash.typeface = Fonts(this).ArialBold

            //set animation circular

            val anim1 = AnimationUtils.loadAnimation(this, R.anim.anim_bot)
            val img = findViewById<View>(R.id.imageView) as ImageView
            mGlobalfuncts!!.glideback(this,R.drawable.androidimage,img)
            img.animation = anim1
            val anim = ObjectAnimator.ofInt(mBarcircle, "progress", 0, 100)
            anim.duration = 3000
            anim.interpolator = DecelerateInterpolator()
            anim.start()
            //handler to view splash for 3 seconds
            val handler = Handler()
            handler.postDelayed({
                mGlobalfuncts!!.gotoActvity(this,MainActivity::class.java,"",1)
                finish()
            }, 3000)
        }
        catch (e:Exception){}

    }

    //funciones supervisoras de la memoria
    override fun onLowMemory() {
        super.onLowMemory()
        Glide.get(this).clearMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Glide.get(this).trimMemory(level)
    }
}
