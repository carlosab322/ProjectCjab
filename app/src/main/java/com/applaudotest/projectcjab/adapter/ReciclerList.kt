package com.applaudotest.projectcjab.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.applaudotest.projectcjab.R
import com.applaudotest.projectcjab.activities.DetailActivity
import com.applaudotest.projectcjab.activities.MainActivity
import com.applaudotest.projectcjab.functions.Globalfun
import com.applaudotest.projectcjab.interfaces.ItemClickListener
import com.applaudotest.projectcjab.realm.JsonFeedBd
import io.realm.RealmResults

class RecyclerPlatHolder(itemView: View?,mGlobalfun: Globalfun?,mContext: Activity?,item: RealmResults<JsonFeedBd>?) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    var txtTitle: TextView = itemView!!.findViewById(R.id.texttitle)
    var txtaddress: TextView = itemView!!.findViewById(R.id.textdir)
    var logo:ImageView = itemView!!.findViewById(R.id.logo)
    var mGlobalfunS = mGlobalfun
    var mContexts= mContext
    var items = item
    private var itemClickListener: ItemClickListener? = null
    init {
        itemView!!.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        try{
            if((mContexts as MainActivity).mislandscape){
                (mContexts as MainActivity).realm_one(adapterPosition+1)
               if((mContexts as MainActivity).mresultrealm!!.size>0){
                   (mContexts as MainActivity).bodyport()
               }

            }
            else{
                //call of detail activity
                mGlobalfunS!!.gotoActvity(mContexts,DetailActivity::class.java,items!![adapterPosition]!!.id.toString(),0)
                itemClickListener!!.onClick(v!!, adapterPosition, false)
            }

        }
        catch (e:Exception){}
    }

}
class RecyclerAdapter(private val item: RealmResults<JsonFeedBd>?,
                       val mContext: Activity?,  val mGlobalfun:Globalfun?,private val firsttime:Boolean?)
    : RecyclerView.Adapter<RecyclerPlatHolder>() {

    /*val animAlpha = AnimationUtils.loadAnimation(mContext,
            R.anim.anim_alpha).start()*/



    private val inflater: LayoutInflater = LayoutInflater.from(mContext)

    override fun onCreateViewHolder(parent: ViewGroup?, p1: Int): RecyclerPlatHolder {
        val itemView = inflater.inflate(R.layout.item_recycler, parent, false)

        return RecyclerPlatHolder(itemView,mGlobalfun,mContext,item)


    }

    override fun getItemCount(): Int {
        //validate first time when show 10 objects
        if(firsttime==true){
            return if(item!!.size>=10){
                (mContext as MainActivity).firstime=false
                10
            } else{
                item.size
            }
        }
        else{
            return item!!.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerPlatHolder?, index: Int) {

        try{
            //fill list
            holder?.txtTitle!!.text = item!![index]!!.team_name
            holder.txtaddress.text = item[index]!!.address
            //load images via glide
            mGlobalfun!!.backgroundurl(mContext!!,item[index]!!.img_logo,holder.logo)
        }
        catch (e:Exception){}

    }

}