package com.example.quickdraw.network

import android.util.Log
import com.example.quickdraw.TAG
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

object  ConnectionManager {
    private var mainIP = "http://192.168.1.63:8000"
    const val DEBUG = true
    private var availableIPs:List<String> = listOf(
        "http://quickdraw.matteogodzilla.net",
        "http://10.10.1.130:8000",
        "http://192.168.1.68:8000",
        "http://192.168.1.63:8000",
        "http://192.168.1.45:8000",
        "http://192.168.1.59:8000",
        "http://10.201.100.225:8000",
        "http://10.174.108.5:8000"
    )

    var errorMessage = ""

    fun getMainIP(): String {
        return mainIP
    }

    private fun query(request:Request,timeout:Int):Response?{
        try{
            val client = OkHttpClient.Builder().connectTimeout(timeout.toLong(), TimeUnit.MILLISECONDS).build()
            val response = client.newCall(request).execute()
            return response
        }
        catch(exception: IOException){
            return null
        }
        catch(exception: SocketTimeoutException){
            return null
        }
    }

    fun setFavourite(ip:String){
        if(!DEBUG){
            mainIP = ip
        }
        else{
            mainIP = "http://192.168.1.63:8000"
        }
    }

    fun attempt(bodyRequest: RequestBody, url: String,isPost:Boolean=true,timeout:Int=1500): Response? {
        Log.i(TAG,"LE PALLE")
        //attempt with main IP
        Log.i(TAG, "Attempting server:$mainIP")
        var request = if(isPost) Request.Builder().url(mainIP+url).post(bodyRequest).build()
            else Request.Builder().url(mainIP+url).get().build()
        var response = query(request,timeout)
        if(!DEBUG){
            if(response==null){
                //attempt with fallback ids
                for(ip in availableIPs){
                    Log.i(TAG, "Attempting server:$ip")
                    if(ip!=mainIP){
                        request = Request.Builder().url(ip+url).post(bodyRequest).build()
                        response = query(request,timeout)
                        if(response!=null){
                            mainIP = ip
                            return response
                        }
                    }
                }
            }
        }
        return response
    }

     fun attemptGet(url:String,timeout:Int=1500):Response?{
        //attempt with main IP
         Log.i(TAG,"LE PALLE")
        var request = Request.Builder().url(mainIP+url).build()
        var response = query(request,timeout)
         Log.i(TAG,response!!.body.toString())
        if(!DEBUG){
            if(response==null){
                //attempt with fallback ids
                for(ip in availableIPs){
                    if(ip!=mainIP){
                        request = Request.Builder().url(ip+url).build()
                        response = query(request,timeout)
                        if(response!=null){
                            mainIP = ip
                            return response
                        }
                    }
                }
            }
        }
        return response
    }
}
