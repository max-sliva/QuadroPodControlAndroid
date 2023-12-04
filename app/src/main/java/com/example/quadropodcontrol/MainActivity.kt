package com.example.quadropodcontrol

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.graphics.blue
import androidx.core.graphics.get
import androidx.core.graphics.green
import androidx.core.graphics.red
import armRotate
import com.example.quadropodcontrol.ui.theme.QuadroPodControlTheme
import legRotate
import kotlin.math.atan

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val quadroPodBody = useResource("quadroPodBody2.PNG") { loadImageBitmap(it) }
        setContent {
            val quadroPodBody = BitmapFactory.decodeResource(
                LocalContext.current.resources,
                R.drawable.quadro_pod_body
            )
            val bodyWidth = quadroPodBody.width
            val bodyHeight = quadroPodBody.height
            println("bodyWidth = $bodyWidth  bodyHeight=$bodyHeight")
            val image = quadroPodBody
            val pixMap = image
//            val fromImage = image[100, 100].

            var rotatePoints = arrayOf<Pair<Int, Int>>()
            for (x in 11 until image.width) { //в циклах ищем зеленые точки, чтоб их добавить к массиву точек поворота
                for (y in 11 until image.height) {
                    if (pixMap[x, y].green in (200..255)) { //todo проверить, ск-ко возвращает
//                println("found green at $x $y")
                        rotatePoints = rotatePoints.plus(Pair(x, y))
                    }
                }
            }
            QuadroPodControlTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    Greeting("Android")
                    App(quadroPodBody.asImageBitmap(), rotatePoints, loadArms())
                }
            }
        }
    }


    //    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun App(
        quadroPodBody: ImageBitmap,
        rotatePoints: Array<Pair<Int, Int>>,
        arms: Array<ImageBitmap>
    ) {
//    var text by remember { mutableStateOf("Hello, World!") }
        //массив с мапом: начальные точки, оффсеты и точки поворота в виде пар значений
//    todo вставить в него все оффсеты, нач.точки и точки поворота
        var arrayForGettingAngles = arrayOf<HashMap<String, Pair<Float, Float>>>()
        var offsetXArray = remember { mutableStateListOf<Float>() }
        var offsetYArray = remember { mutableStateListOf<Float>() }
        repeat(4) {
            offsetXArray.add(0F)
            offsetYArray.add(0F)
        }
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }
        var degs by remember { mutableStateOf(0f) }
        var angleOnDragEnd by remember { mutableStateOf(0f) }
        var arm1RotatePointX by remember { mutableStateOf(0f) }
        var arm1RotatePointY by remember { mutableStateOf(0f) }
        var startPointXArray = remember { mutableStateListOf<Float>() }
        var startPointYArray = remember { mutableStateListOf<Float>() }
//    var legStartPointXArray = remember { mutableStateListOf<Float>() }
//    var legStartPointYArray = remember { mutableStateListOf<Float>() }
        var degsForLegs =
            remember { mutableStateListOf<Float>() } //массив для хранения углов для каждой leg
        repeat(4) {
            startPointXArray.add(0F)
            startPointYArray.add(0F)
//        legStartPointXArray.add(0F)
//        legStartPointYArray.add(0F)
            degsForLegs.add(0F)
        }
        var startPointX by remember { mutableStateOf(0f) }
        var startPointY by remember { mutableStateOf(0f) }
        val openDialog = remember { mutableStateOf(false) }
        var curArm by remember { mutableStateOf(-1) }
//    print(" angle at start = $degs")
//        MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize(), //заполняем всё доступное пространство
            horizontalAlignment = Alignment.CenterHorizontally, //по центру горизонтально
//            verticalArrangement = Arrangement.Center //и вертикально
        ) {
            if (openDialog.value) MakeAlertDialog(
                curArm.toString(),
                openDialog,
                degsForLegs[curArm]
//                legStartPointXArray[curArm],
//                legStartPointYArray[curArm]
            ) { x -> //ф-ия обратного вызова для запоминания угла
                degsForLegs[curArm] = x
//                legStartPointYArray[curArm] = y
//                println("degsForLegs = $x ")
            } //для вызова окна с нужной leg
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
                            var number = getArmNumber(startPointX, quadroPodBody, startPointY)
                            startPointXArray[number] = startPointX
                            startPointYArray[number] = startPointY
                            offsetXArray[number] = offsetX
                            offsetYArray[number] = offsetY
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
//                            println("in listener x    = ${dragAmount.x}  y = ${dragAmount.y}  ")
//                            println("arm1RotatePointX = $arm1RotatePointX arm1RotatePointY = $arm1RotatePointY" )
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                            var number = 0
                            number = getArmNumber(
                                startPointX,
                                quadroPodBody,
                                startPointY
                            )    //для leg4
                            offsetXArray[number] += dragAmount.x
                            offsetYArray[number] += dragAmount.y
                        },
                        onDragEnd = {
//                            println("angle on drag end = $degs")
//                           angleOnDragEnd = degs
                        },
                    )
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {//при клике на нужной arm
//                            println("x = ${it.x}  y = ${it.y}")
                            var number = 0
                            if (it.x < quadroPodBody.width / 2 && it.y < quadroPodBody.height / 2) number =
                                0 //для arm1
                            else if (it.x < quadroPodBody.width / 2 && it.y > quadroPodBody.height / 2) number =
                                1 //для arm2
                            else if (it.x > quadroPodBody.width / 2 && it.y < quadroPodBody.height / 2) number =
                                2   //для третьей лапы
                            else if (it.x > quadroPodBody.width / 2 && it.y > quadroPodBody.height / 2) number =
                                3    //для четвертой лапы
//                            println("leg = $number")
                            curArm = number
                            openDialog.value = true
                        }
                    )
                }
            ) {
//                val canvasQuadrantSize = size / 2F
                try {
                    drawImage(
                        image = quadroPodBody,
                        topLeft = Offset(0F, 0F)
                    )
                    val arm1 = arms[0]
                    armRotate(
                        1,
                        0F,
                        0F,
                        arm1,
                        startPointXArray[0],
                        startPointYArray[0],
                        offsetXArray[0],
                        offsetYArray[0],
                        rotatePoints[0]
                    )
//                    armRotate(0F,0F,arm1, startPointX, startPointY, offsetX, offsetY, rotatePoints)
                    val arm2 = arms[1]
//                    val x0ForArm2 = rotatePoints[1].first
//                    val y0ForArm2 = rotatePoints[1].second-rotatePoints[0].second
                    val y0ForArm2 = rotatePoints[1].second - 80 //позиционируем вторую лапу
                    armRotate(
                        2,
                        0F,
                        y0ForArm2.toFloat(),
                        arm2,
                        startPointXArray[1],
                        startPointYArray[1],
                        offsetXArray[1],
                        offsetYArray[1],
                        rotatePoints[1]
                    )
                    val arm3 = arms[2]
                    val x0ForArm3 = rotatePoints[2].first - 40 //позиционируем третью лапу
                    armRotate(
                        3,
                        x0ForArm3.toFloat(),
                        7F,
                        arm3,
                        startPointXArray[2],
                        startPointYArray[2],
                        offsetXArray[2],
                        offsetYArray[2],
                        rotatePoints[2]
                    )
                    val arm4 = arms[3]
                    val x0ForArm4 = rotatePoints[3].first - 40
                    val y0ForArm4 = rotatePoints[3].second - 55 //позиционируем четвертую лапу
                    armRotate(
                        4,
                        x0ForArm4.toFloat(),
                        y0ForArm4.toFloat(),
                        arm4,
                        startPointXArray[3],
                        startPointYArray[3],
                        offsetXArray[3],
                        offsetYArray[3],
                        rotatePoints[3]
                    )
                } catch (e: NullPointerException) {
//                    Toast.makeText(applicationContext,"No image", Toast.LENGTH_LONG).show()
                    println("No image")
                }
            }
        }
//        }
    }

    private fun getArmNumber(
        startPointX: Float,
        quadroPodBody: ImageBitmap,
        startPointY: Float
    ): Int {
        var number = 0
        if (startPointX < quadroPodBody.width / 2 && startPointY < quadroPodBody.height / 2)
            number = 0 //для leg1
        else if (startPointX < quadroPodBody.width / 2 && startPointY > quadroPodBody.height / 2)
            number = 1 //для leg2
        else if (startPointX > quadroPodBody.width / 2 && startPointY < quadroPodBody.height / 2)
            number = 2   //для leg3
        else if (startPointX > quadroPodBody.width / 2 && startPointY > quadroPodBody.height / 2)
            number = 3
        return number
    }

    //    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun MakeAlertDialog(
        curArm: String,
        openDialog: MutableState<Boolean>,
        degsInLeg: Float,
//    startPointX: Float,
//    startPointY: Float,
        onUpdate: (x: Float) -> Unit
    ) { //показываем окно с нужным leg для его поворота
//    var degsInDialog = 0f
        var degs by remember { mutableStateOf(0f) }
        degs = degsInLeg

        AlertDialog(
            onDismissRequest = { //действия при закрытии окна
                openDialog.value = false
                onUpdate(degs)
                println("Exit")
            },
            modifier = Modifier.fillMaxSize(),
            title = { Text(text = curArm) }, //заголовок окна
            text = { //внутренняя часть окна
                val backImage = BitmapFactory.decodeResource(
                    LocalContext.current.resources,
                    R.drawable.back
                )
                val resID= //todo разобраться, почему такой способ возвращает null
                         //если не получится, загружать в самом начале в массив и здесь получать по нужному индексу
                    resources.getIdentifier("leg${curArm.toInt() + 1}_body_.PNG", "drawable",
                        packageName
                    )
                var legBody: Bitmap? = null
                if (resID!=0) legBody = BitmapFactory.decodeResource(
                    LocalContext.current.resources,
                    resID
                )
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

                val resID_leg=
                    resources.getIdentifier("leg${curArm.toInt() + 1}.PNG", "drawable",
                        packageName
                    )
                var leg: Bitmap? = null
                if (resID_leg!=0) legBody = BitmapFactory.decodeResource(
                    LocalContext.current.resources,
                    resID_leg
                )

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
//                    val resID=
//                        resources.getIdentifier("leg${curArm.toInt() + 1}.PNG", "drawable",
//                            packageName
//                        )
//                    var leg: Bitmap? = null
//                    if (resID!=0) legBody = BitmapFactory.decodeResource(
//                        LocalContext.current.resources,
//                        resID
//                    )
//                    val leg =
//                        useResource("leg${curArm.toInt() + 1}.PNG") { loadImageBitmap(it) }//сама рука
//                println("leg image width = ${leg.width}")
                    var rotatePointLeg: Pair<Int, Int>? = null
                    val pixMapForLeg = leg
                    for (x in 11 until leg!!.width) { //в циклах ищем зеленые точки, чтоб их добавить к массиву точек поворота
                        for (y in 11 until leg.height) {
                            if ((pixMapForLeg!![x, y].green in (200..255) && curArm.toInt() != 0)
                                || (curArm.toInt() == 0 && pixMapForLeg[x, y].green in (200..255) && pixMapForLeg[x, y].red < 85 && pixMapForLeg[x, y].blue < 85)
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
                            topLeft = Offset(0F, 0F)
                        )
//                    println("curArm = $curArm")
                        if (curArm.toInt() == 0 || curArm.toInt() == 2) {
                            legRotate(curArm.toInt(), degs, leg.asImageBitmap(), rotatePointLeg!!, rotatePoint!!)
                            drawImage(
                                image = legBody!!.asImageBitmap(),
                                topLeft = Offset(0F, 0F)
                            )
                        } else {
                            drawImage(
                                image = legBody!!.asImageBitmap(),
                                topLeft = Offset(0F, 0F)
                            )
                            legRotate(curArm.toInt(), degs, leg.asImageBitmap(), rotatePointLeg!!, rotatePoint!!)
                        }
                    } catch (e: NullPointerException) {
//                    Toast.makeText(applicationContext,"No image", Toast.LENGTH_LONG).show()
                        println("No image")
                    }
//                val degs = angle(armRotatePointX, armRotatePointY + y0, startPointX, startPointY, offsetX, offsetY)
                }
            },
            confirmButton = { //кнопка Ok, которая будет закрывать окно
                Button(onClick = {
                    openDialog.value = false
                    onUpdate(degs)
                    println("Ok pressed")
                })
                { Text(text = "OK") }
            }
        )
    }

    fun angle(
        arm1RotatePointX: Float,
        arm1RotatePointY: Float,
        startPointX: Float,
        startPointY: Float,
        offsetX: Float,
        offsetY: Float
    ): Float { //ф-ия для получения угла поворота лапы
        var degs: Float
        //вычисляем катеты для угла поворота
        val katet1 = arm1RotatePointX - (startPointX + offsetX)
//    val katet1 = arm1RotatePointX + (startPointX + offsetX)
        val katet2 = startPointY + offsetY - arm1RotatePointY
        val tan = katet2 / kotlin.math.abs(katet1) //тангенс угла поворота
//                    print(" offsetY = $offsetY   offsetX = $offsetX")
//    print(" katet2 = $katet2   katet1 = $katet1")
        if (offsetY.toInt() != 0)
            degs = Math.toDegrees(atan(tan).toDouble()).toFloat() //сам угол поворота
        else degs = 0F
        println("angle = $degs")
        return degs
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        QuadroPodControlTheme {
            Greeting("Android")
        }
    }

    @Composable
    fun loadArms(): Array<ImageBitmap> { //для загрузки изображений ног робота (плечи)
        var armImagesArray = arrayOf<ImageBitmap>()
        var arm = BitmapFactory.decodeResource(
            LocalContext.current.resources,
            R.drawable.arm1
        )
        armImagesArray = armImagesArray.plus(arm.asImageBitmap())
        arm = BitmapFactory.decodeResource(
            LocalContext.current.resources,
            R.drawable.arm2
        )
        armImagesArray = armImagesArray.plus(arm.asImageBitmap())
        arm = BitmapFactory.decodeResource(
            LocalContext.current.resources,
            R.drawable.arm3
        )
        armImagesArray = armImagesArray.plus(arm.asImageBitmap())
        arm = BitmapFactory.decodeResource(
            LocalContext.current.resources,
            R.drawable.arm4
        )
        armImagesArray = armImagesArray.plus(arm.asImageBitmap())
//        for (i in 0..3) {
//            val armID = resources.getIdentifier("arm${i + 1}.PNG", "drawable", packageName)
////            var leg: Bitmap? = null
//            if (armID!=0)
////            val arm = useResource("arm${i + 1}.PNG") { loadImageBitmap(it) }
//                armImagesArray = armImagesArray.plus(BitmapFactory.decodeResource(
//                    LocalContext.current.resources,
//                    armID
//                ).asImageBitmap())
//            else println("arm$i not found")
//        }
//    armImagesArray.forEach {
//        println("arm = ${it.height}")
//    }
        return armImagesArray
    }
}