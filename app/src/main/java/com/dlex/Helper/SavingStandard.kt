package com.dlex.Helper

import android.content.Context
import com.dlex.Model.Alert


class SavingStandard(context: Context) {

    private val standard= context.getSharedPreferences("mainCurrency", 0)
    private val update = standard.edit()
    private val singleDate = SingleDate()
    private val jsonUpdate = JsonUpdate(context)

    fun currentSave(mainCurrency: String): Boolean {
        val newValue = fetchCurrent(mainCurrency)
        val date = singleDate.updatingDate(singleDate.showDateDigit())
        val subKey = "$date $mainCurrency"
        val currentList = HashSet<String>()
        val body = standard.getStringSet("savingList", null)
        if (body == null) {
            currentList.add(subKey)
            update.putString(subKey, newValue)
            update.putStringSet("savingList", currentList)
            update.apply()
            return true
        } else {
            for (i in body) {
                if (i.equals(subKey)) {
                    return false
                }
            }
            currentList.addAll(body)
            currentList.add(subKey)
            update.putString(subKey, newValue)
            update.putStringSet("savingList", currentList)
            update.apply()
            return true
        }

    }

    private fun fetchCurrent(mainCurrency: String): String {
        var body: String? = null
        var count = 0
        while (body == null) {
            body = standard.getString(mainCurrency, null)
            count += 1
            if (body != null) {
                return body
            } else if (count == 10000000) {
                return ""
            }
        }
        return ""
    }

    fun alertSave(alertName: String, data: Alert): Boolean {
        val newValue = jsonUpdate.createJson(data)
        val currentList = HashSet<String>()
        val body = standard.getStringSet("alertList", null)
        if (body == null) {
            currentList.add(alertName)
            update.putString(alertName, newValue)
            update.putStringSet("alertList", currentList)
            update.apply()
            return true
        } else if (body.size < 10) {
            for (i in body) {
                if (i.equals(alertName)) {
                    return false
                }
            }
            currentList.addAll(body)
            currentList.add(alertName)
            update.putString(alertName, newValue)
            update.putStringSet("alertList", currentList)
            update.apply()
            return true
        }
        return false
    }

    fun searchSave(mainCurrency: String, searchDate: String): Boolean {
        val newValue = jsonUpdate.initializeNewJson(mainCurrency, searchDate)
        val date = singleDate.updatingDate(searchDate)
        val subKey = "$date $mainCurrency"
        val currentList = HashSet<String>()
        val body = standard.getStringSet("savingList", null)
        if (body == null) {
            currentList.add(subKey)
            update.putString(subKey, newValue)
            update.putStringSet("savingList", currentList)
            update.apply()
            return true
        } else {
            for (i in body) {
                if (i.equals(subKey)) {
                    return false
                }
            }
            currentList.addAll(body)
            currentList.add(subKey)
            update.putString(subKey, newValue)
            update.putStringSet("savingList", currentList)
            update.apply()
            return true
        }
    }

    fun deletingSaveList(subKey: String) {
        val currentList = HashSet<String>()
        val body = standard.getStringSet("savingList", null)
        update.remove(subKey)
        update.apply()
        for (i in body!!) {
            if (!subKey.equals(i)) {
                currentList.add(i)
            }
        }
        update.putStringSet("savingList", currentList)
        update.apply()
    }

    fun deletingAlertList(alertName: String) {
        val currentList = HashSet<String>()
        val body = standard.getStringSet("alertList", null)
        update.remove(alertName)
        update.apply()
        for (i in body!!) {
            if (!alertName.equals(i)) {
                currentList.add(i)
            }
        }
        update.putStringSet("alertList", currentList)
        update.apply()
    }

}