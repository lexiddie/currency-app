package com.dlex.Helper

import com.google.gson.GsonBuilder
import com.dlex.Model.CountryName
import okhttp3.*
import java.io.IOException

class Geolocation {

    fun getGeolocation(callback: (String) -> Unit) {
        val url = "http://api.ipstack.com/check?access_key=d75b02b7df106a5125956e5d658c0255&format=1"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()!!.string()
                val gson = GsonBuilder().create()
                val data = gson.fromJson(body, CountryName::class.java)
                callback(data.country_name)
            }
            override fun onFailure(call: Call, e: IOException) {

            }
        })
    }
}