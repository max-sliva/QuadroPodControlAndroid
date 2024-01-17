package com.example.quadropodcontrol

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap

class ArmsAndLegsControl : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val quadroPodBody = ContextCompat.getDrawable(this, R.drawable.quadro_pod_body)?.toBitmap()
            Image(
                bitmap = quadroPodBody?.asImageBitmap()!!,
                contentDescription = "Image",
                modifier = Modifier.fillMaxSize()
            )
            val arm1 = ContextCompat.getDrawable(this, R.drawable.arm1)?.toBitmap()
            val width = arm1!!.width
            val height = arm1.height
            var foundGreen = false
            var xGreenOnArm by remember {mutableStateOf(0f)}
            var yGreenOnArm by remember {mutableStateOf(0f)}
            for (x in 0 until width) {
                for (y in 0 until height) {
                    val pixel = arm1.getPixel(x, y)
                    val green = Color.green(pixel)
                    if (green >= 200) {
                        xGreenOnArm = x.toFloat()
                        yGreenOnArm = y.toFloat()
                        println("found green in $x $y")
                        foundGreen = true
                        break
                    }
                }
                if (foundGreen) break
            }
//            Image(
//                bitmap = arm1?.asImageBitmap()!!,
//                contentDescription = "Image",
////                modifier = Modifier.fillMaxSize()
//            )
//            RotatedImage(bitmap = arm1)
            TransformableSample(arm1, xGreenOnArm, yGreenOnArm)
//            QuadroPodControlTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
////                    Greeting2("Android")
//                    Image(
//                        bitmap = bitmap?.asImageBitmap()!!,
//                        contentDescription = "Image",
//                        modifier = Modifier.fillMaxSize()
//                    )
//                }
//            }
        }
    }
}

@Composable
private fun TransformableSample(bitmapSrc: Bitmap?, xGreenOnArm: Float, yGreenOnArm: Float) {
    // set up all transformation states
//    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { _, offsetChange, rotationChange ->
//        scale *= zoomChange
//        rotation += rotationChange
        rotation += offsetChange.y
//        offset += offsetChange
    }
    val scale = 0.5f
    Image(
        bitmap = bitmapSrc?.asImageBitmap()!!,
        contentDescription = "Image",
        modifier = Modifier
            .graphicsLayer(
                transformOrigin = TransformOrigin(0.5f, 0.1f),
                scaleX = scale,
                scaleY = scale,
                rotationZ = rotation / 10,
//                translationX = offset.x,
//                translationY = offset.y
            )
            .transformable(state = state)
    )
}
