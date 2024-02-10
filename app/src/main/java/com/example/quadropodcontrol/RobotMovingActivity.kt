package com.example.quadropodcontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.quadropodcontrol.ui.theme.QuadroPodControlTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sendDataToBluetoothDevice
import kotlin.math.sqrt

class RobotMovingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var i = 0
            var currentDirection by  remember { mutableStateOf("0") }
            this.lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    // this block is automatically executed when moving into
                    // the started state, and cancelled when stopping.
                    while (true) {
                        i++
//                        println("$i second from coroutine")
                        if ( currentDirection!="0") {
                            val toArduino = "$currentDirection" + "\n"
                            println("toArduino = $toArduino")
                            if (BluetoothWork.currentSocket != null) {
                                sendDataToBluetoothDevice(BluetoothWork.currentSocket!!, toArduino)
                            }
                        }
                        delay(500L)
                    }
                }
            }
            QuadroPodControlTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ControlBox("Robot Moving"){ x-> currentDirection = x}
                }
            }
        }
    }
}

@Composable
fun ControlBox(name: String, onDirectionChange: (x: String) -> Unit) {
    var startPointX by remember { mutableStateOf(0f) }
    var ratio by remember {   mutableStateOf(0f)     }
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

    Canvas(modifier = Modifier
        .fillMaxSize()
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
                    val dist = sqrt(Math.pow((circleXStart-circleX).toDouble(), 2.0) + Math.pow((circleYStart-circleY).toDouble(), 2.0))
                    if (dist+innerCircleRadius < outerCircleRadius) {
                        oldCircleX = circleX
                        oldCircleY = circleY
                        circleX += dragAmount.x
                        circleY += dragAmount.y

                    }
                    else {
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
        }){
        val canvasWidth = size.width
        val canvasHeight = size.height
        if (circleX==0f){
            circleX = canvasWidth / 2
            circleY = canvasHeight / 2
            circleXStart = circleX
            circleYStart = circleY
        }
        if (circleX+innerCircleRadius+50 < canvasWidth / 2 ) currentDirection = "l"
        if (circleX-innerCircleRadius-50 > canvasWidth / 2 ) currentDirection = "r"
        if (currentDirection!="0") {
            println("currentDirection = $currentDirection")
//            if (BluetoothWork.currentSocket!=null) sendDataToBluetoothDevice(BluetoothWork.currentSocket!!, "$currentDirection"+"\n")
        }
        else println("stop")
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


        drawCircle( // рисуем
            Color.Gray, //цвет рисования
            radius = outerCircleRadius //и радиус
        )
        drawCircle( // рисуем
            Color.Blue, //цвет рисования
            radius = innerCircleRadius, //и радиус
            center = Offset(circleX, circleY)
        )
    }
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
}
