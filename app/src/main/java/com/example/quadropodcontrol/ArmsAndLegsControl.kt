package com.example.quadropodcontrol

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
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
            val arm2 = ContextCompat.getDrawable(this, R.drawable.arm2)?.toBitmap()
            val width = arm1!!.width
            val height = arm1.height
            var foundGreen = false
            var xGreenOnArm by remember {mutableStateOf(0f)}
            var yGreenOnArm by remember {mutableStateOf(0f)}
//            for (x in 0 until width) {
//                for (y in 0 until height) {
//                    val pixel = arm1.getPixel(x, y)
//                    val green = Color.green(pixel)
//                    if (green >= 200) {
//                        xGreenOnArm = x.toFloat()
//                        yGreenOnArm = y.toFloat()
//                        println("found green in $x $y")
//                        foundGreen = true
//                        break
//                    }
//                }
//                if (foundGreen) break
//            }
//            RotatedImage(bitmap = arm1)
            TransformableSample("arm1", arm1, xGreenOnArm, yGreenOnArm, LocalContext.current)
            TransformableSample("arm2", arm2, xGreenOnArm, yGreenOnArm, LocalContext.current)
            //todo add all arms
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
private fun TransformableSample(
    armName: String,
    bitmapSrc: Bitmap?,
    xGreenOnArm: Float,
    yGreenOnArm: Float,
    current: Context
) {
    // set up all transformation states
//    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val scale = 0.5f
    var transformOrigin: TransformOrigin? = null
    var translateY = -210F
    var rangeUp = 790F
    var rangDown = -400F
    when (armName) {
        "arm1"->  {
            transformOrigin = TransformOrigin(0.9f, 0.7f)
            translateY = -210F
            rangeUp = 800F
            rangDown = -400F
        }
        "arm2"->  {
            transformOrigin = TransformOrigin(0.9f, 0.3f)
            translateY = 490F
            rangeUp = 400F
            rangDown = -800F
        }
        "arm3"->  {
            transformOrigin = TransformOrigin(0.9f, 0.7f)
        }
        "arm4"->  {
            transformOrigin = TransformOrigin(0.9f, 0.7f)
        }
    }
    val state = rememberTransformableState { _, offsetChange, rotationChange ->
//        scale *= zoomChange
        var temp = rotation
        temp+=-offsetChange.y
        if (temp in rangDown..rangeUp) rotation += -offsetChange.y
        println("rotation = $rotation")
//        offset += offsetChange
    }
    Image(
        bitmap = bitmapSrc?.asImageBitmap()!!,
        contentDescription = "Image",
        modifier = Modifier
            .graphicsLayer(
//                transformOrigin = TransformOrigin(0.9f, 0.7f),
                transformOrigin = transformOrigin!!,
                scaleX = scale,
                scaleY = scale,
                rotationZ = rotation / 10,
                translationX = -440F,
                translationY = translateY
            )
            .transformable(state = state)
            .clickable {
                Toast.makeText(current, "$armName clicked", Toast.LENGTH_LONG).show()
            }
    )
}
