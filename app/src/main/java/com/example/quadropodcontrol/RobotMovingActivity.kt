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
import com.example.quadropodcontrol.ui.theme.QuadroPodControlTheme
import kotlin.math.sqrt

class RobotMovingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuadroPodControlTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ControlBox("Robot Moving")
                }
            }
        }
    }
}

@Composable
fun ControlBox(name: String, modifier: Modifier = Modifier) {
    var startPointX by remember { mutableStateOf(0f) }
    var ratio by remember {   mutableStateOf(0f)     }
    var startPointY by remember { mutableStateOf(0f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var circleX by remember { mutableStateOf(0f) }
    var circleY by remember { mutableStateOf(0f) }
    var circleXStart by remember { mutableStateOf(0f) }
    var circleYStart by remember { mutableStateOf(0f) }

    val innerCircleRadius = 100f
    val outerCircleRadius = 400f

    Canvas(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
//                    detectTapGestures(
//                        onLongPress = {
//                            println("x = ${it.x}  y = ${it.y}")
//                        }
//                    )
//                   if (degs<=65)
            detectDragGestures(
                onDragStart = { touch ->
//                            println("\nStart of the interaction is x=${touch.x} y=${touch.y}")
                    startPointX = touch.x
                    startPointY = touch.y
                    offsetX = 0F //сбрасываем оффсеты, чтобы нормально двигать ногу
                    offsetY = 0F
//                            val ratio = quadroPodBody.width / size.width
//                    println("ratio in dragStart = $ratio")

//                    var number =
//                        getArmNumber(startPointX, quadroPodBody, startPointY, ratio)
//                    startPointXArray[number] = startPointX
//                    startPointYArray[number] = startPointY
//                    offsetXArray[number] = offsetX
//                    offsetYArray[number] = offsetY
                },
                onDrag = { change, dragAmount ->
                    change.consume()
//                            println("in listener x    = ${dragAmount.x}  y = ${dragAmount.y}  ")
//                            println("arm1RotatePointX = $arm1RotatePointX arm1RotatePointY = $arm1RotatePointY" )
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                    val dist = sqrt(Math.pow((circleXStart-circleX).toDouble(), 2.0) + Math.pow((circleYStart-circleY).toDouble(), 2.0))
                    if (dist+innerCircleRadius < outerCircleRadius) {
                        circleX += dragAmount.x
                        circleY += dragAmount.y
                    }

//                    println("dragAmount.x = ${dragAmount.x}  dragAmount.y = ${dragAmount.y}")
                    println("circleX = $circleX  circleY = $circleY")
                    var number = 0
//                            val ratio = quadroPodBody.width / size.width
//                    println("ratio in dragStart = $ratio")
//                    number = getArmNumber(
//                        startPointX,
//                        quadroPodBody,
//                        startPointY,
//                        ratio
//                    )    //для leg4
//                    offsetXArray[number] += dragAmount.x
//                    offsetYArray[number] += dragAmount.y
                },
                onDragEnd = {
                    circleX = circleXStart
                    circleY = circleYStart
//                            println("angle on drag end = $degs")
//                           angleOnDragEnd = degs
//                            val ratio = quadroPodBody.width / size.width
//                    val number = getArmNumber(
//                        startPointX,
//                        quadroPodBody,
//                        startPointY,
//                        ratio
//                    )
//                    if (curDeviceName != "") writeArmAngleToArduino(
//                        bltWork,
//                        socketToDevice!!,
//                        number,
//                        degsForArms[number]
//                    )
                },
            )
        }){
        val canvasWidth = size.width
        val canvasHeight = size.height
//        val innerCircleRadius = canvasWidth / 8f
//        val outerCircleRadius = canvasWidth / 3f
        if (circleX==0f){
            circleX = canvasWidth / 2
            circleY = canvasHeight / 2
            circleXStart = circleX
            circleYStart = circleY
        }
        if (innerCircleRadius+offsetX<=outerCircleRadius && innerCircleRadius+offsetY<=outerCircleRadius) {
//            circleX += offsetX
//            circleY += offsetY
//            println("offsetX = $offsetX  offsetY = $offsetY")
        }

        drawCircle( //то рисуем его
            Color.Gray, //цвет рисования
            radius = outerCircleRadius //и радиус
        )
        drawCircle( //то рисуем его
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
