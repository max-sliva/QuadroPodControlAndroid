package com.example.quadropodcontrol
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.get
import androidx.core.graphics.green
import kotlinx.coroutines.*

var end = 0
fun myTimer(n: Int) =  runBlocking{
    println("Starting timer $n...")
//    withContext(Dispatchers.IO) {
        // Perform some long-running task here
        delay(5000) // Pause the coroutine for 3 seconds
//    }
    println("Timer finished!")
    end = 1
}



fun imageWork(localContext: Context): Pair<Bitmap, Array<Pair<Int, Int>>>  {
    val quadroPodBody = BitmapFactory.decodeResource(
            localContext.resources,
            R.drawable.quadro_pod_body
    )
    var rotatePoints = arrayOf<Pair<Int, Int>>()

//    GlobalScope.launch (Dispatchers.IO) {
        val bodyWidth = quadroPodBody.width
        val bodyHeight = quadroPodBody.height
        println("bodyWidth = $bodyWidth  bodyHeight=$bodyHeight")
//            println("screen width = ${size.width.toInt()}, size.height.toInt()")
        val image = quadroPodBody
        val pixMap = image
//            val fromImage = image[100, 100].

        for (x in 11 until image.width) { //в циклах ищем зеленые точки, чтоб их добавить к массиву точек поворота
            for (y in 11 until image.height) {
                if (pixMap[x, y].green in (178..255)) { //todo проверить, ск-ко возвращает
//                println("found green at $x $y")
                    rotatePoints = rotatePoints.plus(Pair(x, y))
                }
            }
        }
//        withContext (Dispatchers.Main) {
//            //update the UI
////            button.isEnabled=true
//        }
//    }
    val bodyAndPoints = Pair<Bitmap, Array<Pair<Int, Int>>>(quadroPodBody, rotatePoints)
    println("done image work")
    return bodyAndPoints
}

fun main(){
    println("begin")
    var i = 1
    GlobalScope.launch{
        myTimer(1)

    }
    i++
    runBlocking{
        myTimer(2)
    }


    println("end")
}