package com.example.quadropodcontrol

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.blue
import androidx.core.graphics.get
import androidx.core.graphics.green
import androidx.core.graphics.red
import angle
import com.example.quadropodcontrol.ui.theme.QuadroPodControlTheme
import curLeg
import curLegBody
import legRotate

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var angle by remember { mutableStateOf(0F) }
            var legNumber: Int? = null
            val extras = intent.extras
            if (extras != null) {
                angle = extras.getFloat("angle")
                legNumber = extras.getInt("legNumber")+1
            }
            println("angle = $angle")
            QuadroPodControlTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LegRotate(legNumber!!, angle)
                }
            }
        }
    }
}

@Composable
fun LegRotate(legNumber:Int, angle: Float?, modifier: Modifier = Modifier) {
    val backImage = BitmapFactory.decodeResource(
        LocalContext.current.resources,
        R.drawable.back
    )
    var degs by remember { mutableStateOf(0f) }
    degs = angle!!
    var legBody: Bitmap? = null
    val res = LocalContext.current.resources
    legBody = curLegBody(legNumber, res)
    val pixMap = legBody
    var rotatePoint: Pair<Int, Int>? = null
    for (x in 11 until legBody!!.width) { //в циклах ищем зеленые точки, чтоб их добавить к массиву точек поворота
        for (y in 11 until legBody.height) {
            if (pixMap!![x, y].green in (200..255) && pixMap[x, y].red < 125 && pixMap[x, y].blue < 255) {
//                        println("found green on leg${curArm.toInt() + 1} at $x $y")
                rotatePoint = Pair(x, y)
            }
        }
    }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var startPointX by remember { mutableStateOf(0f) }
    var startPointY by remember { mutableStateOf(0f) }

    var leg: Bitmap? = curLeg(legNumber, res)

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { touch ->
//                            println("\nStart of the interaction is x=${touch.x} y=${touch.y}")
//                                onUpdate(touch.x, touch.y)
                        startPointX = touch.x
                        startPointY = touch.y

                        offsetX = 0F //сбрасываем оффсеты, чтобы нормально двигать ногу
                        offsetY = 0F
                        var number = 0
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
//                            println("in listener x    = ${dragAmount.x}  y = ${dragAmount.y}  ")
//                            println("arm1RotatePointX = $arm1RotatePointX arm1RotatePointY = $arm1RotatePointY" )
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                        degs = angle(
                            rotatePoint!!.first.toFloat(),
                            rotatePoint.second.toFloat(),
                            startPointX,
                            startPointY,
                            offsetX,
                            offsetY
                        )
//                                println("angle = $degs")
                    },
                    onDragEnd = { },
                )
            }
    ) {
        var rotatePointLeg: Pair<Int, Int>? = null
        val pixMapForLeg = leg
        for (x in 11 until leg!!.width) { //в циклах ищем зеленые точки, чтоб их добавить к массиву точек поворота
            for (y in 11 until leg.height) {
                if ((pixMapForLeg!![x, y].green in (200..255) && legNumber != 0)
                    || (legNumber == 0 && pixMapForLeg[x, y].green in (200..255) && pixMapForLeg[x, y].red < 85 && pixMapForLeg[x, y].blue < 85)
                ) {
//                        println("found green on leg${curArm.toInt() + 1} at $x $y")
                    rotatePointLeg = Pair(x, y)
                }
            }
        }
//                println("for leg pair.x = ${rotatePointLeg?.first}, pair.y = ${rotatePointLeg?.second}")
//                val degs = angle(rotatePoint!!.first.toFloat(), rotatePoint.second.toFloat(), startPointX, startPointY, offsetX, offsetY)
//                println("angle for leg = $degs")
        try {
            drawImage(
                image = backImage.asImageBitmap(),
                dstSize = IntSize(size.width.toInt(), size.height.toInt())
//                topLeft = Offset(0F, 0F)
            )
//                    println("curArm = $curArm")
            if (legNumber == 0 || legNumber == 2) {
                legRotate(legNumber, degs, leg.asImageBitmap(), rotatePointLeg!!, rotatePoint!!)
                drawImage(
                    image = legBody!!.asImageBitmap(),
                    topLeft = Offset(0F, 0F)
                )
            } else {
                drawImage(
                    image = legBody!!.asImageBitmap(),
                    topLeft = Offset(0F, 0F)
                )
                legRotate(legNumber, degs, leg.asImageBitmap(), rotatePointLeg!!, rotatePoint!!)
            }
        } catch (e: NullPointerException) {
//                    Toast.makeText(applicationContext,"No image", Toast.LENGTH_LONG).show()
            println("No image")
        }
//                val degs = angle(armRotatePointX, armRotatePointY + y0, startPointX, startPointY, offsetX, offsetY)
    }
//    Text(
//        text = "Hello $angle!",
//        modifier = modifier
//    )
}
