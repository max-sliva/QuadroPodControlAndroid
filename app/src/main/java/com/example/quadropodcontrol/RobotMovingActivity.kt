package com.example.quadropodcontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.quadropodcontrol.ui.theme.QuadroPodControlTheme
import sendDataToBluetoothDevice
import kotlin.math.pow
import kotlin.math.sqrt


class RobotMovingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var i = 0
            var currentDirection by  remember { mutableStateOf("0") }
//            this.lifecycleScope.launch {
//                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                    // this block is automatically executed when moving into
//                    // the started state, and cancelled when stopping.
//                    while (true) {
//                        i++
////                        println("$i second from coroutine")
//                        if ( currentDirection!="0") {
//                            val toArduino = "$currentDirection" + "\n"
//                            println("toArduino = $toArduino")
//                            if (BluetoothWork.currentSocket != null) {
//                                sendDataToBluetoothDevice(BluetoothWork.currentSocket!!, toArduino)
//                            }
//                        }
//                        delay(500L)
//                    }
//                }
//            }
            QuadroPodControlTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    Box(
                    Column(
                        modifier = Modifier.fillMaxSize(), //заполняем всё доступное пространство
//                        .border(BorderStroke(2.dp, Color.Blue)),
                        horizontalAlignment = Alignment.CenterHorizontally, //по центру горизонтально
                        //verticalArrangement = Arrangement.Center
                    ) {
                            EyesControlBox("Eyes Moving")
                            MovingControlBox("Robot Moving"){ x-> currentDirection = x}

                    }
                }
            }
        }
    }
}

@Composable
fun EyesControlBox(
    name: String,
//    onDirectionChange: (x: String) -> Unit
) {
    var tappedEyes by remember {
        mutableStateOf("Center")
    }
    val alingList = mapOf("Top" to Alignment.TopCenter, "Left" to Alignment.CenterStart,
                          "Center" to Alignment.Center, "Right" to Alignment.CenterEnd, "Bottom" to Alignment.BottomCenter,
                          "Inner" to Alignment.BottomStart, "Out" to Alignment.BottomEnd )
    val eyesList = mapOf("Top" to R.drawable.eyes_up, "Left" to R.drawable.eyes_left,
                         "Center" to R.drawable.eyes_center, "Right" to R.drawable.eyes_right, "Bottom" to R.drawable.eyes_down,
                         "Inner" to R.drawable.eyes_in, "Out" to R.drawable.eyes_out)
    val toArduinoStrings = mapOf("Top" to "eyesUp", "Left" to "eyesLeft", "Right" to "eyesRight",
                                 "Center" to "eyesForward", "Bottom" to "eyesDown", "Inner" to "eyesIn",
                                 "Out" to "eyesOut")
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(140.dp)
    ){
        eyesList.forEach { (eyeLocation, eyeImage) ->
            Image(//нужен import androidx.compose.foundation.Image
                  modifier = Modifier.align(alingList[eyeLocation]!!)
                      .border(BorderStroke(2.dp, if (tappedEyes.contains(eyeLocation)) Color.Green else Color.White))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                tappedEyes = eyeLocation
//                                val toArduino = if (currentDirection != "0") "$currentDirection" + "\n" else "s\n"
                                val toArduino = toArduinoStrings[eyeLocation] + "\n"
                                if (BluetoothWork.currentSocket != null) {
                                    sendDataToBluetoothDevice(BluetoothWork.currentSocket!!, toArduino)
                                }
                            }
                        )
                    },
                  painter = painterResource(id = eyeImage), //указываем источник изображения
                  contentDescription = "", //можно вставить описание изображения
                  contentScale = ContentScale.Fit, //параметры масштабирования изображения
            )
        }
    }
}

@Composable
fun MovingControlBox(
    name: String,
//    modifier: Modifier,
    onDirectionChange: (x: String) -> Unit) {
    var startPointX by remember { mutableStateOf(0f) }
//    var ratio by remember {   mutableStateOf(0f)     }
    var startPointY by remember { mutableStateOf(0f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var circleX by remember { mutableStateOf(0f) }
    var circleY by remember { mutableStateOf(0f) }
    var circleXStart by remember { mutableStateOf(0f) }
    var circleYStart by remember { mutableStateOf(0f) }
    var oldCircleX = 0f
    var oldCircleY = 0f
    val innerCircleRadius = 150f
    val outerCircleRadius = 400f
    var currentDirection by  remember { mutableStateOf("0") }
    var oldDirection by  remember { mutableStateOf("0") }

        Canvas(modifier = Modifier
            .fillMaxHeight()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { touch ->
//                            println("\nStart of the interaction is x=${touch.x} y=${touch.y}")
                        startPointX = touch.x
                        startPointY = touch.y
                        offsetX = 0F //сбрасываем оффсеты, чтобы нормально двигать ногу
                        offsetY = 0F
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                        val dist = sqrt(
                            (circleXStart - circleX).toDouble().pow(2.0) + (circleYStart - circleY).toDouble().pow(2.0)
                        )
                        if (dist + innerCircleRadius < outerCircleRadius) {
                            oldCircleX = circleX
                            oldCircleY = circleY
                            circleX += dragAmount.x
                            circleY += dragAmount.y

                        } else {
                            circleX = oldCircleX
                            circleY = oldCircleY
//                        println("dist+innerCircleRadius = ${dist+innerCircleRadius}  outerCircleRadius = $outerCircleRadius")
                        }
//                    println("circleX = $circleX  circleY = $circleY")
                    },
                    onDragEnd = {
                        circleX = circleXStart
                        circleY = circleYStart
                        currentDirection = "0"
                    },
                )
            }) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            if (circleX == 0f) {
                circleX = canvasWidth / 2
                circleY = canvasHeight / 2
                circleXStart = circleX
                circleYStart = circleY
            }
            if (circleX + innerCircleRadius + 50 < canvasWidth / 2) currentDirection = "l"
            if (circleX - innerCircleRadius - 50 > canvasWidth / 2) currentDirection = "r"
            if (circleY + innerCircleRadius + 50 < canvasHeight / 2) currentDirection = "f"
            if (circleY - innerCircleRadius - 50 > canvasHeight / 2) currentDirection = "b"
            if (currentDirection != oldDirection) {
                val toArduino = if (currentDirection != "0") "$currentDirection" + "\n" else "s\n"
                println("toArduino = $toArduino")
                if (BluetoothWork.currentSocket != null) {
                    sendDataToBluetoothDevice(BluetoothWork.currentSocket!!, toArduino)
                }
                oldDirection = currentDirection
            }
//        if (currentDirection!="0") {
//            println("currentDirection = $currentDirection")
//            if (BluetoothWork.currentSocket!=null) sendDataToBluetoothDevice(BluetoothWork.currentSocket!!, "$currentDirection"+"\n")
//        }
//        else println("stop")
            onDirectionChange(currentDirection)
//            lifecycleScope?.launch {
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


            drawControl(outerCircleRadius, innerCircleRadius, circleX, circleY)
        }
//    }
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
}

//@Composable
private fun DrawScope.drawControl(
    outerCircleRadius: Float,
    innerCircleRadius: Float,
    circleX: Float,
    circleY: Float
) {

    drawCircle( // рисуем внешний круг
        Color.Gray, //цвет рисования
        radius = outerCircleRadius, //и радиус
//        center = Offset(circleX, circleY)
    )
    drawCircle( // рисуем внутренний круг
        Color.Blue, //цвет рисования
        radius = innerCircleRadius, //и радиус
        center = Offset(circleX, circleY)
    )
}
