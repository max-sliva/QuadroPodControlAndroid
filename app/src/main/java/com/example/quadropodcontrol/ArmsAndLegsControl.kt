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
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
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
            val back = ContextCompat.getDrawable(this, R.drawable.back)?.toBitmap()
            var showLeg by remember { mutableStateOf(false) }
            var armNumber by remember {
                mutableStateOf(0)
            }
            val arm1 = ContextCompat.getDrawable(this, R.drawable.arm1)?.toBitmap()
            val arm2 = ContextCompat.getDrawable(this, R.drawable.arm2)?.toBitmap()
            val arm3 = ContextCompat.getDrawable(this, R.drawable.arm3)?.toBitmap()
            val arm4 = ContextCompat.getDrawable(this, R.drawable.arm4)?.toBitmap()
            val leg1 = ContextCompat.getDrawable(this, R.drawable.leg1)?.toBitmap()
            val leg2 = ContextCompat.getDrawable(this, R.drawable.leg2)?.toBitmap()
            val leg3 = ContextCompat.getDrawable(this, R.drawable.leg3)?.toBitmap()
            val leg4 = ContextCompat.getDrawable(this, R.drawable.leg4)?.toBitmap()
            val legsArray = arrayOf(leg1, leg2, leg3, leg4)
            val leg1_body = ContextCompat.getDrawable(this, R.drawable.leg1_body_)?.toBitmap()
            val leg2_body = ContextCompat.getDrawable(this, R.drawable.leg2_body_)?.toBitmap()
            val leg3_body = ContextCompat.getDrawable(this, R.drawable.leg3_body_)?.toBitmap()
            val leg4_body = ContextCompat.getDrawable(this, R.drawable.leg4_body_)?.toBitmap()
            val legBodiesArray = arrayOf(leg1_body, leg2_body, leg3_body, leg4_body)
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
            arrayOf(arm1, arm2, arm3, arm4).forEachIndexed { index, bitmap ->
                ArmRotation("arm${index+1}", bitmap, LocalContext.current)
                { x, number ->
                    showLeg = x
                    armNumber = number.digitToInt()
                }
            }

            if (showLeg) {
                LegMoving(back, legsArray[armNumber-1], legBodiesArray[armNumber-1], armNumber){ x-> showLeg = x}
            }
        }
    }
}


@Composable
private fun LegMoving(
    back: Bitmap?,
 //   armNumber: Char,
    leg: Bitmap?,
    legBody: Bitmap?,
    armNumber: Int,
    onXClick: (x: Boolean) -> Unit
) {
    if (armNumber%2 == 1) {
        Image(
            bitmap = back?.asImageBitmap()!!,
            contentDescription = "Image",
            modifier = Modifier
                .fillMaxSize()
            //            .width(Dp(2000f))
            //            .size(1900.dp)

        )
        LegRotaion(leg){}
        Image(
            bitmap = legBody?.asImageBitmap()!!,
            contentDescription = "Image",
            modifier = Modifier
                .fillMaxSize()
            //            .width(Dp(2000f))
//                        .size(1900.dp)
        )
    }
    else {
        Image(
            bitmap = legBody?.asImageBitmap()!!,
            contentDescription = "Image",
            modifier = Modifier
                .fillMaxSize()
            //            .width(Dp(2000f))
            //            .size(1900.dp)

        )
        LegRotaion(leg){}
    }
    Button(onClick = { onXClick(false) }) {
        Text(text = "x")
    }
}

@Composable
private fun LegRotaion(
//    legName: String,
    bitmapSrc: Bitmap?,
//    xGreenOnArm: Float,
//    yGreenOnArm: Float,
//    current: Context,
    onRotate: (angle: Int)-> Unit
){
    //todo доделать нормальное вращение для leg
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val scale = 0.5f
    var transformOrigin: TransformOrigin? = TransformOrigin(0.9f, 0.7f)
    var translateY = -210F
    var translateX = -440F
    var rangeUp = 790F
    var rangDown = -400F
    val state = rememberTransformableState { _, offsetChange, rotationChange ->
//        scale *= zoomChange
        var temp = rotation
//        if (armName!="arm3" && armName!="arm4") {
            temp += -offsetChange.y
            if (temp in rangDown..rangeUp) rotation += -offsetChange.y
//        }
//        else {
//            temp+=offsetChange.y
//            if (temp in rangDown..rangeUp) rotation += offsetChange.y
//        }
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
                translationX = translateX,
                translationY = translateY
            )
            .transformable(state = state)
    )
}

@Composable
private fun ArmRotation(
    armName: String,
    bitmapSrc: Bitmap?,
//    xGreenOnArm: Float,
//    yGreenOnArm: Float,
    current: Context,
    onClick:(x: Boolean, number: Char) -> Unit
) {
    // set up all transformation states
//    var showLeg by remember { mutableStateOf(false) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val scale = 0.5f
    var transformOrigin: TransformOrigin? = null
    var translateY = -210F
    var translateX = -440F
    var rangeUp = 790F
    var rangDown = -400F
    when (armName) {
        "arm1"->  {
            transformOrigin = TransformOrigin(0.9f, 0.7f) //это определяет точку поворота, первый параметр по X, второй - по Y
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
            transformOrigin = TransformOrigin(0.1f, 0.7f)
            translateX = 1120F
            rangeUp = 400F
            rangDown = -800F
        }
        "arm4"->  {
            transformOrigin = TransformOrigin(0.1f, 0.3f)
            translateX = 1130F
            translateY = 530F
            rangeUp = 800F
            rangDown = -400F
        }
    }
    val state = rememberTransformableState { _, offsetChange, rotationChange ->
//        scale *= zoomChange
        var temp = rotation
        if (armName!="arm3" && armName!="arm4") {
            temp += -offsetChange.y
            if (temp in rangDown..rangeUp) rotation += -offsetChange.y
        }
        else {
            temp+=offsetChange.y
            if (temp in rangDown..rangeUp) rotation += offsetChange.y
        }
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
                translationX = translateX,
                translationY = translateY
            )
            .transformable(state = state)
            .clickable {
                Toast
                    .makeText(current, "$armName clicked", Toast.LENGTH_LONG)
                    .show()
//                showLeg = true
                onClick(true, armName.last())
            }
    )
}
