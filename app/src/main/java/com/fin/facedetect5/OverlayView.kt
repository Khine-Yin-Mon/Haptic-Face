package com.fin.facedetect5

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.util.Size
import android.view.View
import androidx.camera.core.CameraSelector
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceLandmark

class OverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val framePaint: Paint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    private var frameRect: Rect? = null
    private var face: Face? = null
    var isFrontCamera: Boolean = false

    fun setImageSize(face : Face) {
        this.face = face
    }

    fun drawFrameAroundFace(boundingBox: Rect) {
        frameRect = transformRect(boundingBox)
        invalidate()
    }

    fun clearFrame() {
        frameRect = null
        invalidate()
    }

    private fun transformRect(originalRect: Rect): Rect {
        face?.let { it ->
            Log.d("FaceAnalyzer", "boundingBox ${it.boundingBox}, ${it.boundingBox.left}, ${it.boundingBox.right}, ${it.boundingBox.top}, ${it.boundingBox.bottom}")

            val left = it.boundingBox.left.toFloat()
            val top = it.boundingBox.top.toFloat()
            val right = it.boundingBox.right.toFloat()
            val bottom = it.boundingBox.bottom.toFloat()

            Log.d("FaceAnalyzer", "transformRect: $left, $top, $right, $bottom")
            return Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
        }

        return originalRect
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        frameRect?.let { rect ->
            canvas.drawRect(rect, framePaint)
        }
    }
}
