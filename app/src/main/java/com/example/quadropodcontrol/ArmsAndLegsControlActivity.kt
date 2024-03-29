package com.example.quadropodcontrol

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import convertAngle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sendDataToBluetoothDevice

class ArmsAndLegsControlActivity : ComponentActivity() {
    @SuppressLint("CoroutineCreationDuringComposition")
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
//            val configuration = LocalConfiguration.current
//            val screenHeight = configuration.screenHeightDp.dp
//            val screenWidth = configuration.screenWidthDp.dp

            println("BluetoothWork.currentSocket = ${BluetoothWork.currentSocket}")
            val loader = BitmapsLoader()
            val armsArray: Array<Bitmap?> = loader.loadArms(this)
            val legsArray = loader.loadLegs(this)
            val legBodiesArray = loader.loadLegsBodies(this)
            var legAnglesArray = remember { mutableStateListOf<Int>(-842, -944, 785, 855) }
//            val width = arm1!!.width
//            val height = arm1.height
            var foundGreen = false
            var xGreenOnArm by remember {mutableStateOf(0f)}
            var yGreenOnArm by remember {mutableStateOf(0f)}
//            var anglesArray = remember { mutableStateListOf<Int>(50, 100, 50, 100) }
            armsArray.forEachIndexed { index, bitmap ->
                ArmRotation("arm${index+1}", bitmap, LocalContext.current)
                { x, number ->
                    showLeg = x
                    armNumber = number.digitToInt()
//                    anglesArray[index] = angle
                }
            }
//            Text(text = anglesArray[0].toString())

            if (showLeg) {
                LegMoving(back, legsArray[armNumber-1], legBodiesArray[armNumber-1], armNumber, legAnglesArray){ x-> showLeg = x}
            }
//            var i = 0
//            this.lifecycleScope.launch {
//                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                    // this block is automatically executed when moving into
//                    // the started state, and cancelled when stopping.
//                    while (true) {
//                        i++
//                        println("$i second from coroutine")
//                        delay(1000L)
//                    }
//                }
//            }
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
    rotation = angle[armNumber-1].toFloat()
    var offset by remember { mutableStateOf(Offset.Zero) }
    val scale = 0.5f
    var transformOrigin: TransformOrigin? = TransformOrigin(0.75f, 0.7f)
    var translateX = -260F
    var translateY = 50F
    //скорректировать границы углов поворота
    var rangeUp = 600F //для armNumber == 1 и 2
    var rangDown = -1200F
    if (armNumber==2) {
        transformOrigin = TransformOrigin(0.85f, 0.6f)
        translateX = 10F
        translateY = 70F
//        rangeUp = 600F
//        rangDown = -1200F
    }
    if (armNumber==3) {
        transformOrigin = TransformOrigin(0.25f, 0.7f)
        translateX = 700F
        translateY = 90F
        rangeUp = 1000F
        rangDown = -600F
    }
    if (armNumber==4) {
        transformOrigin = TransformOrigin(0.25f, 0.6f)
        translateX = 300F
        translateY = 90F
        rangeUp = 1200F
        rangDown = -600F
    }
    var angleToArduino by remember { mutableStateOf(0)}
    angleToArduino = convertAngle(rotation.toInt(), IntRange(rangDown.toInt(), rangeUp.toInt()), IntRange(0, 180))
    val legNumberForArduino = if (armNumber==4) 3 else if (armNumber==3) 4 else armNumber
    if (legNumberForArduino*2-1==3 || legNumberForArduino*2-1==5) angleToArduino=180-angleToArduino

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
//        val legNumberForArduino = if (armNumber==4) 3 else if (armNumber==3) 4 else armNumber //меняем 3 и 4 местами, из-за подключения на Arduino
        angleToArduino = convertAngle(rotation.toInt(), IntRange(rangDown.toInt(), rangeUp.toInt()), IntRange(0, 180))
        if (legNumberForArduino*2-1==3 || legNumberForArduino*2-1==5) angleToArduino=180-angleToArduino
        val toArduino = "${legNumberForArduino*2-1}-$angleToArduino\n"
        println("to Arduino = $toArduino")
        if (BluetoothWork.currentSocket!=null) sendDataToBluetoothDevice(BluetoothWork.currentSocket!!, "$toArduino")

//        offset += offsetChange
    }
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    Text(
        text = angleToArduino.toString(),
        modifier = Modifier
            .offset(0.dp,screenHeight-100.dp) //отступ для текстового поля в зависимости от номера мотора
    )
    Image(
        bitmap = bitmapSrc?.asImageBitmap()!!,
        contentDescription = "Image",
        modifier = Modifier
            .graphicsLayer(
//                transformOrigin = TransformOrigin(0.9f, 0.7f),
                transformOrigin = transformOrigin!!,
                scaleX = scale,
                scaleY = scale,
                rotationZ = (angle[armNumber - 1] / 10).toFloat(),
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
//    anglesArray: SnapshotStateList<Int>,
    onClick: (x: Boolean, number: Char) -> Unit
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
    //начальное значение для вывода в Text для угла arm
    var angle by remember { mutableStateOf( if (armName.last().digitToInt()==1 || armName.last().digitToInt()==4) 50 else 100 )}
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
    var armNumberForArduino = armName.last().digitToInt() - 1 //для строки "arm1" получаем число 0
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
//        val angle =180-convertAngle(rotation.toInt(), IntRange(rangDown.toInt(), rangeUp.toInt()), IntRange(30, 170))
        angle = convertAngle(rotation.toInt(), IntRange(rangDown.toInt(), rangeUp.toInt()), IntRange(5, 150))
//        if (anglesArray[armName.last().digitToInt()-1]!=angle) {
//            anglesArray[armName.last().digitToInt()-1] = angle
//        }
        println("rotation = $rotation  angle = $angle  rangDown = $rangDown  rangeUp = $rangeUp")
//        var armNumberForArduino = armName.last().code - '0'.code - 1
//        anglesArray[armNumberForArduino] = angle
        if (armNumberForArduino==2) armNumberForArduino = 3
        else if (armNumberForArduino==3) armNumberForArduino = 2
//        val toArduino = "${(armName.last().code-'0'.code-1)*2}-$angle\n"
        val toArduino = "${armNumberForArduino*2}-$angle\n"
        println("to Arduino = $toArduino")
        if (BluetoothWork.currentSocket!=null) sendDataToBluetoothDevice(BluetoothWork.currentSocket!!, "$toArduino")
//        writeArmAngleToArduino()
//        offset += offsetChange
    }
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val textX = if (armNumberForArduino == 2 || armNumberForArduino == 3) screenWidth / 2 - 40.dp + 50.dp else  screenWidth / 2 - 40.dp
    val textY = if (armNumberForArduino == 1 || armNumberForArduino == 3) (screenHeight / 2 + 20.dp) else screenHeight /2 - 50.dp
    println("textX = $textX textY = $textY")
    Text(
        text = angle.toString(),
        modifier = Modifier
            .offset(textX,textY) //отступ для текстового поля в зависимости от номера мотора
    )
    
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
                    .makeText(current, "$armName clicked angle = $angle", Toast.LENGTH_LONG)
                    .show()
//                showLeg = true
//                anglesArray[armName
//                    .last()
//                    .digitToInt() - 1] = angle
                onClick(true, armName.last())
            }
//            .pointerInput(Unit) {
//                detectDragGestures(
//                    onDragStart = {
//
//                    },
//                    onDrag = { change: PointerInputChange, dragAmount: Offset ->
////                        offsetX += dragAmount.x
////                        offsetY += dragAmount.y
//
//                    },
//                    onDragEnd = {
//                        println("drag ended")
//                    }
////                    var interaction: DragInteraction.Start? = null
////                    onDragEnd = {
////                    }
//                )
//            }
    )
}
