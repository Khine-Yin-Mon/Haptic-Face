package com.fin.facedetect5.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.fin.facedetect5.util.BoudingBoxUtil
import com.google.mlkit.vision.face.Face

class CustomFaceView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    var face: Face? = null
    private lateinit var canvas: Canvas

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        this.canvas = canvas

        if (face != null) {
            BoudingBoxUtil.drawBoundingBox(canvas, face!!)
        } else {
            BoudingBoxUtil.clearBoundingBoxes(canvas)
        }
    }

    fun updateFace(face: Face?) {
        this.face = face
        invalidate()  // This will trigger a redraw by calling onDraw
    }

}