package com.dlex.Helper

import android.view.View
import android.widget.ImageView
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.graphics.BitmapFactory

object CircleImage {

    fun setImage(view: View, image: Int, imgView: ImageView) {
        val bitMap = BitmapFactory.decodeResource(view.resources, image)
        val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(view.resources, bitMap)
        circularBitmapDrawable.setCircular(true)
        imgView.setImageDrawable(circularBitmapDrawable)
    }
}