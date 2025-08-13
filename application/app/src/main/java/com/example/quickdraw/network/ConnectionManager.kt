package com.example.quickdraw.network

import android.util.Log
import androidx.core.text.util.LocalePreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.quickdraw.PrefKeys
import com.example.quickdraw.TAG
import io.ktor.http.Url
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.IOException

object  ConnectionManager {
    private var mainIP = "http://192.168.1.45:8000"
    private var availableIPs:List<String>  = listOf(
        "http://10.10.1.130:8000",
        "http://192.168.1.68:8000",
        "http://192.168.1.63:8000",
        "http://192.168.1.45:8000"
    )

    fun getMainIP(): String {
        return mainIP
    }

    private fun query(request:Request):Response?{
        try{
            val client = OkHttpClient.Builder().build()
            val response = client.newCall(request).execute()
            return response
        }
        catch(exception: IOException){
            return null
        }
    }

    fun setFavourite(ip:String){
        mainIP = ip
    }

    fun AttemptQuery(bodyRequest: RequestBody, url: String): Response? {
        //attempt with main IP
        var request = Request.Builder().url(mainIP+url).post(bodyRequest).build()
        var response = query(request)
        if(response==null){
            //attempt with fallback ids
            for(ip in availableIPs){
                Log.i(TAG, "Attempting server:$ip")
                if(ip!=mainIP){
                    request = Request.Builder().url(ip+url).post(bodyRequest).build()
                    response = query(request)
                    if(response!=null){
                        mainIP = ip
                        return response
                    }
                }
            }
        }
        else{
            return response
        }
        return null
    }

    public fun AttemptEmptyQuery(url:String):Response?{
        //attempt with main IP
        var request = Request.Builder().url(mainIP+url).build()
        var response = query(request)
        if(response==null){
            //attempt with fallback ids
            for(ip in availableIPs){
                if(ip!=mainIP){
                    request = Request.Builder().url(ip+url).build()
                    response = query(request)
                    if(response!=null){
                        mainIP = ip
                        return response
                    }
                }
            }
        }
        else{
            return response
        }
        return null
    }
}