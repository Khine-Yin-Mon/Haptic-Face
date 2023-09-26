package com.fin.facedetect5.util

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.mlkit.vision.face.Face


class BoudingBoxUtil {
    companion object {
        private val boundingBoxPaint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 4.0f
        }

        fun drawBoundingBox(canvas: Canvas, face: Face) {
            val x1 = face.boundingBox.left.toFloat() * 2
            val y1 = face.boundingBox.top.toFloat() * 3
            val x2 = face.boundingBox.right.toFloat() * 2
            val y2 = face.boundingBox.bottom.toFloat() * 3
            val boundingBox = android.graphics.RectF(x1, y1, x2, y2)
            canvas.drawRect(boundingBox, boundingBoxPaint)
        }

        fun drawBoundingBoxes(canvas: Canvas, faces: List<Face>) {
            for (face in faces) {
                drawBoundingBox(canvas, face)
            }
        }

        fun clearBoundingBoxes(canvas: Canvas) {
            canvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR)
        }
    }
}