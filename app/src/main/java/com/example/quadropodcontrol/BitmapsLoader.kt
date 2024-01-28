package com.example.quadropodcontrol

import android.graphics.Bitmap
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap

class BitmapsLoader {
    fun loadArms(armsAndLegsControl: ArmsAndLegsControl): Array<Bitmap?> {
        val arm1 = ContextCompat.getDrawable(armsAndLegsControl, R.drawable.arm1)?.toBitmap()
        val arm2 = ContextCompat.getDrawable(armsAndLegsControl, R.drawable.arm2)?.toBitmap()
        val arm3 = ContextCompat.getDrawable(armsAndLegsControl, R.drawable.arm3)?.toBitmap()
        val arm4 = ContextCompat.getDrawable(armsAndLegsControl, R.drawable.arm4)?.toBitmap()
        return arrayOf(arm1, arm2, arm3, arm4)
    }

    fun loadLegs(armsAndLegsControl: ArmsAndLegsControl): Array<Bitmap?> {
        val leg1 = ContextCompat.getDrawable(armsAndLegsControl, R.drawable.leg1)?.toBitmap()
        val leg2 = ContextCompat.getDrawable(armsAndLegsControl, R.drawable.leg2)?.toBitmap()
        val leg3 = ContextCompat.getDrawable(armsAndLegsControl, R.drawable.leg3)?.toBitmap()
        val leg4 = ContextCompat.getDrawable(armsAndLegsControl, R.drawable.leg4)?.toBitmap()
        return arrayOf(leg1, leg2, leg3, leg4)
    }

    fun loadLegsBodies(armsAndLegsControl: ArmsAndLegsControl): Array<Bitmap?> {
        val leg1_body = ContextCompat.getDrawable(armsAndLegsControl, R.drawable.leg1_body_)?.toBitmap()
        val leg2_body = ContextCompat.getDrawable(armsAndLegsControl, R.drawable.leg2_body_)?.toBitmap()
        val leg3_body = ContextCompat.getDrawable(armsAndLegsControl, R.drawable.leg3_body_)?.toBitmap()
        val leg4_body = ContextCompat.getDrawable(armsAndLegsControl, R.drawable.leg4_body_)?.toBitmap()
        return arrayOf(leg1_body, leg2_body, leg3_body, leg4_body)
    }
}