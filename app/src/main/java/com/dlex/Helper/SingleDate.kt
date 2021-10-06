package com.dlex.Helper

import android.annotation.SuppressLint
import android.util.Log
import java.text.DateFormat
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class SingleDate {

    @SuppressLint("SimpleDateFormat")
    fun showDateDigit(): String {
        val dateFormat = SimpleDateFormat("dd MM yyyy")
        val currentDate = dateFormat.format(Date())
        return currentDate
    }

    @SuppressLint("SimpleDateFormat")
    fun showDateDisplay(): String {
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM")
        val currentDate = dateFormat.format(Date())
        return currentDate
    }

    @SuppressLint("SimpleDateFormat")
    fun showYearDisplay(): String {
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy")
        val currentDate = dateFormat.format(Date())
        return currentDate
    }

    fun updatingDate(date: String): String {
        val dateSplited = date.split(" ")
        return (dateSplited[2] + "-" + dateSplited[1] + "-" + dateSplited[0])
    }

    fun updatingDisplay(date: String): String {
        val dateSplit = date.split("-")
        val day = dateSplit[2].toInt()
        val month = DateFormatSymbols().months[dateSplit[1].toInt() - 1]
        val year = dateSplit[0].toInt()
        return "$month ${String.format("%02d", day)}, $year"
    }
}