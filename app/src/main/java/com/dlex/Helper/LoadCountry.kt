package com.dlex.Helper

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import com.dlex.Model.CountryMobile
import com.dlex.Model.CurrencyData
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.lang.StringBuilder

object LoadCountry {
    private var carrierInfo = CarrierInfo()
    private var loadJson = LoadJson
    private val gson = GsonBuilder().create()

    private fun getCountry(context: Context): String {
        val defaultCountry = "United States"
        val country = carrierInfo.getCarrierCountry(context)
        val json = loadJson.loadJsonFromAsset(context, "Json/CountriesData.json")
        if (country != "") {
            for (i in 0..json!!.length() - 1) {
                val element = json.getJSONObject(i)
                val countryMobile = gson.fromJson(element.toString(), CountryMobile::class.java)
                if (countryMobile.code.equals(country)) {
                    return countryMobile.name
                }
            }
        }
        return defaultCountry
    }

    fun getCurrencyCode(context: Context): String? {
        val countryName = getCountry(context)
        val dataArray = arrayOf("Json/CountryCurrency.json", "Json/EuroCountries.json")
        for (i in 0..dataArray.size - 1) {
            val json = loadJson.loadJsonFromAsset(context, dataArray[i])
            for (j in 0..json!!.length() -1) {
                val element = json.getJSONObject(j)
                val currency = gson.fromJson(element.toString(), CurrencyData::class.java)
                if (currency.name.equals(countryName)) {
                    return currency.code
                }
            }
        }
        return "USD"
    }

    fun getCurrencyName(context: Context, code: String): String? {
        val json = loadJson.loadJsonFromAsset(context, "Json/CountryCurrency.json")
        for (j in 0..json!!.length() -1) {
            val element = json.getJSONObject(j)
            val currency = gson.fromJson(element.toString(), CurrencyData::class.java)
            if (currency.code.equals(code)) {
                return currency.currency
            }
        }
        return null
    }


}