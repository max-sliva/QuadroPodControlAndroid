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
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import convertAngle

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
            val loader = BitmapsLoader()
            val armsArray: Array<Bitmap?> = loader.loadArms(this)
            val legsArray = loader.loadLegs(this)
            val legBodiesArray = loader.loadLegsBodies(this)
            var legAnglesArray = remember { mutableStateListOf<Int>(0,0,0,0) }
//            val width = arm1!!.width
//            val height = arm1.height
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
            armsArray.forEachIndexed { index, bitmap ->
                ArmRotation("arm${index+1}", bitmap, LocalContext.current)
                { x, number ->
                    showLeg = x
                    armNumber = number.digitToInt()
                }
            }

            if (showLeg) {
                LegMoving(back, legsArray[armNumber-1], legBodiesArray[armNumber-1], armNumber, legAnglesArray){ x-> showLeg = x}
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
    legAnglesArray: SnapshotStateList<Int>,
    onXClick: (x: Boolean) -> Unit
) {
//    var angle by remember { mutableIntStateOf(0) }
//    angle = legAnglesArray[armNumber-1]
    if (armNumber%2 == 1) {
        Image(
            bitmap = back?.asImageBitmap()!!,
            contentDescription = "Image",
            modifier = Modifier
                .fillMaxSize()
            //            .width(Dp(2000f))
            //            .size(1900.dp)

        )
        //todo передавать в LegRotaion remember для хранения угла
        LegRotaion(leg, armNumber, legAnglesArray) { /*x-> println(x) */}
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
        LegRotaion(leg, armNumber, legAnglesArray){}
    }
    Button(onClick = {
            onXClick(false)
          //  legAnglesArray[armNumber-1] =
           }) {
        Text(text = "x")
    }
}

@Composable
private fun LegRotaion(
//    legName: String,
    bitmapSrc: Bitmap?,
    armNumber: Int,
    angle: SnapshotStateList<Int>,
//    xGreenOnArm: Float,
//    yGreenOnArm: Float,
//    current: Context,
    onRotate: (angle: Int)-> Unit
){
    var rotation by remember { mutableFloatStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val scale = 0.5f
    var transformOrigin: TransformOrigin? = TransformOrigin(0.75f, 0.7f)
    var translateX = -260F
    var translateY = 50F
    var rangeUp = 800F
    var rangDown = -800F
    if (armNumber==2) {
        transformOrigin = TransformOrigin(0.85f, 0.6f)
        translateX = 10F
        translateY = 70F
    }
    if (armNumber==3) {
        transformOrigin = TransformOrigin(0.25f, 0.7f)
        translateX = 700F
        translateY = 90F
    }
    if (armNumber==4) {
        transformOrigin = TransformOrigin(0.25f, 0.6f)
        translateX = 300F
        translateY = 90F
    }
    val state = rememberTransformableState { _, offsetChange, rotationChange ->
//        scale *= zoomChange
        var temp = rotation
        if (armNumber in 1..2 ){
            temp += -offsetChange.y
            if (temp in rangDown..rangeUp) rotation += -offsetChange.y
        }
        else {
            temp+=offsetChange.y
            if (temp in rangDown..rangeUp) rotation += offsetChange.y
        }
        angle[armNumber-1] = rotation.toInt()
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
                rotationZ = (angle[armNumber-1] / 10).toFloat(),
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
        val angle = convertAngle(rotation.toInt(), IntRange(rangDown.toInt(), rangeUp.toInt()), IntRange(30, 170))
        println("rotation = $rotation  angle = $angle  rangDown = $rangDown  rangeUp = $rangeUp")
//        writeArmAngleToArduino()
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
