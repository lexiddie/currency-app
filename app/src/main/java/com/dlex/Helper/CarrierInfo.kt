package com.dlex.Helper

import android.content.Context
import android.telephony.TelephonyManager

class CarrierInfo {

    fun getCarrierCountry(context: Context): String {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val countryISO = telephonyManager.simCountryIso
        return if (countryISO is String) {
            countryISO.toUpperCase()
        } else {
            ""
        }
    }
}