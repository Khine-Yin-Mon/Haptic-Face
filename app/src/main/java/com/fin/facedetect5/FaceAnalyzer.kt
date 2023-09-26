package com.fin.facedetect5

import android.graphics.PointF
import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark

class FaceAnalyzer(private var listener: (Int, Boolean, Face?) -> Unit) :
    ImageAnalysis.Analyzer {
    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()

    private val detector = FaceDetection.getClient(options)


    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {

        val mediaImage = imageProxy.image ?: return
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        var face: Face? = null

        detector.process(image)
            .addOnSuccessListener { faces ->
                var isFrontFacing = false
                val allContours = mutableListOf<PointF>()
                val faceContours = mutableListOf<PointF>()
                val lipUpTopContours = mutableListOf<PointF>()
                val lipUpBottomContours = mutableListOf<PointF>()
                val lipDownTopContours = mutableListOf<PointF>()
                val lipDownBottomContours = mutableListOf<PointF>()

                val contourTypes = listOf(
                    FaceContour.FACE,
                    FaceContour.UPPER_LIP_TOP,
                    FaceContour.UPPER_LIP_BOTTOM,
                    FaceContour.LOWER_LIP_TOP,
                    FaceContour.LOWER_LIP_BOTTOM
                )

                var isFullFace = false
                if (faces.size > 0) {
                    Log.d("FaceAnalyzer", "faces.size: ${faces.size}")
                    face = faces[0]
                    val leftEye = face?.getLandmark(FaceLandmark.LEFT_EYE)
                    val rightEye = face?.getLandmark(FaceLandmark.RIGHT_EYE)
                    val leftEar = face?.getLandmark(FaceLandmark.LEFT_EAR)
                    val rightEar = face?.getLandmark(FaceLandmark.RIGHT_EAR)
                    val mouthLeft = face?.getLandmark(FaceLandmark.MOUTH_LEFT)
                    val mouthRight = face?.getLandmark(FaceLandmark.MOUTH_RIGHT)
                    val noseBase = face?.getLandmark(FaceLandmark.NOSE_BASE)
                    Log.d("FaceAnalyzer", "leftEye: $leftEye")

                    isFullFace = leftEye != null && rightEye != null &&
                            leftEar != null && rightEar != null &&
                            mouthLeft != null && mouthRight != null &&
                            noseBase != null
                    for (contourType in contourTypes) {
                        val contour = face?.getContour(contourType)?.points
                        contour?.let {
                            when (contourType) {
                                FaceContour.FACE -> faceContours.addAll(it)
                                FaceContour.UPPER_LIP_TOP -> lipUpTopContours.addAll(it)
                                FaceContour.UPPER_LIP_BOTTOM -> lipUpBottomContours.addAll(it)
                                FaceContour.LOWER_LIP_TOP -> lipDownTopContours.addAll(it)
                                FaceContour.LOWER_LIP_BOTTOM -> lipDownBottomContours.addAll(it)
                            }
                            allContours.addAll(it)
                        }
                    }
                    for(i in 0 until faceContours.size) {
                        when (i) {
                            in 0..8 -> {
                                Log.d("FaceAnalyzer", "RigthUp Face[$i]: ${faceContours[i]}")
                            }
                            in 9..16 -> {
                                Log.d("FaceAnalyzer", "RigthDown Face[$i]: ${faceContours[i]}")
                            }
                            in 17..26 -> {
                                Log.d("FaceAnalyzer", "LeftDown Face[$i]: ${faceContours[i]}")
                            }
                            in 27..35 -> {
                                Log.d("FaceAnalyzer", "LeftUp Face[$i]: ${faceContours[i]}")
                            }
                        }
                    }
                    for(i in 0 until lipUpTopContours.size){
                        when (i) {
                            in 0..5 -> {
                                Log.d("FaceAnalyzer", "LeftDown LipUpTop[$i]: ${lipUpTopContours[i]}")
                            }
                            in 6..10 -> {
                                Log.d("FaceAnalyzer", "RightDown LipUpTop[$i]: ${lipUpTopContours[i]}")
                            }
                        }
                    }
                    for(i in 0 until lipDownTopContours.size){
                        when(i){
                            in 0..4 -> {
                                Log.d("FaceAnalyzer", "LeftDown LipDownTop[$i]: ${lipDownTopContours[i]}")
                            }
                            in 5..8 -> {
                                Log.d("FaceAnalyzer", "RightDown LipDownTop[$i]: ${lipDownTopContours[i]}")
                            }
                        }
                    }
                    for(i in 0 until lipDownTopContours.size){
                        when(i){
                            in 0 .. 4 -> {
                                Log.d("FaceAnalyzer", "RightDown LipDownBottom[$i]: ${lipDownBottomContours[i]}")
                            }
                            in 5 .. 8 -> {
                                Log.d("FaceAnalyzer", "LeftDown LipDownBottom[$i]: ${lipDownBottomContours[i]}")
                            }
                        }
                    }
                    for(i in 0 until lipDownBottomContours.size){
                        when(i){
                            in 0 .. 5 -> {
                                Log.d("FaceAnalyzer", "RightDown LipDownBottom[$i]: ${lipDownBottomContours[i]}")
                            }
                            in 6 .. 10 -> {
                                Log.d("FaceAnalyzer", "LeftDown LipDownBottom[$i]: ${lipDownBottomContours[i]}")
                            }
                        }
                    }

                    isFrontFacing = if (isFullFace) {
                        val yaw = face?.headEulerAngleY
                        val roll = face?.headEulerAngleZ
                        (kotlin.math.abs(yaw!!) < 5) && (kotlin.math.abs(roll!!) < 5)
                    } else {
                        false
                    }
                }
                listener(faces.size, isFrontFacing, face)

            }
            .addOnFailureListener { e ->
                Log.e("FaceAnalyzer", "Face detection failure.", e)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

}

