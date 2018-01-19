package com.applaudotest.projectcjab.realm

import android.util.Log
import io.realm.Realm
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class RealmImporter(internal var data: InputStream?, internal var realm: Realm?) {

    var transactionTime: TransactionTime?=null
    //save data into realm
    fun importfromjsonload() {
        //transaction timer
        val srformat = SimpleDateFormat("hhmmss", Locale.US)
        val format = srformat.format(Date())
        println("inicio"+format)
        transactionTime = TransactionTime()
        transactionTime!!.start = System.currentTimeMillis()
        realm!!.executeTransaction { realm ->
            try {
                try {
                    //update data via json from service
                    realm.createOrUpdateAllFromJson(JsonFeedBd::class.java,  data!!)
                } catch (e1: Exception) {
                    realm.cancelTransaction()
                }
                transactionTime!!.end = System.currentTimeMillis()
                println("fin"+format)
            } catch (e: Exception) {
                realm.cancelTransaction()
            }
        }
        val startTimeSeconds = System.currentTimeMillis().toInt() / 1000
        println("Time elapsed: ")
        println(transactionTime!!.duration.toInt() / 1000 - startTimeSeconds)
        Log.d("Realm", "createOrUpdateAllFromJson Task completed in " + transactionTime!!.duration + "ms")

    }
}