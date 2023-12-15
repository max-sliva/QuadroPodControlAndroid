package com.example.quadropodcontrol

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
//import androidx.compose.material.DropdownMenu
//import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.blue
import androidx.core.graphics.get
import androidx.core.graphics.green
import androidx.core.graphics.red
import armRotate
import com.example.quadropodcontrol.ui.theme.QuadroPodControlTheme
//import curLegBody
//import legRotate
import writeArmAngleToArduino
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
//            println("screen width = ${size.width.toInt()}, size.height.toInt()")
            val image = quadroPodBody
            val pixMap = image
//            val fromImage = image[100, 100].

            var rotatePoints = arrayOf<Pair<Int, Int>>()
            for (x in 11 until image.width) { //в циклах ищем зеленые точки, чтоб их добавить к массиву точек поворота
                for (y in 11 until image.height) {
                    if (pixMap[x, y].green in (178..255)) { //todo проверить, ск-ко возвращает
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
                    val res = LocalContext.current.resources
                    App(quadroPodBody.asImageBitmap(), rotatePoints, loadArms(res))
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

//        val imageWidth = quadroPodBody.width * LocalDensity.current.density
//        val imageHeight = quadroPodBody.width * LocalDensity.current.density

        var bltList = listOf<String>() //список имен устройств
        val bltWork = BluetoothWork(LocalContext.current)
        Toast.makeText(LocalContext.current, "get devices", Toast.LENGTH_LONG).show()
        var pairedDevices = remember { mutableSetOf<BluetoothDevice>() }
        pairedDevices = bltWork.getBluetoothDevices() { list-> bltList=list} as MutableSet<BluetoothDevice>  //сами устройства
        var socketToDevice: BluetoothSocket? by remember { mutableStateOf(null) }
        println("blt devices = ${bltList}")
        println("rotatePoints = ${rotatePoints.toList()}")
        var curDeviceName by remember { mutableStateOf("")        }
//        var curBluetoothDevice by remember {  mutableStateOf(null)  }

        var degsForArms = remember { mutableStateListOf<Int>() } //массив для хранения углов для каждой arm
        //вставляем углы для arms
        degsForArms.add(100)
        degsForArms.add(60)
        degsForArms.add(80)
        degsForArms.add(140)
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
        val mContext = LocalContext.current
//    print(" angle at start = $degs")
//        MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize(), //заполняем всё доступное пространство
            horizontalAlignment = Alignment.CenterHorizontally, //по центру горизонтально
//            verticalArrangement = Arrangement.Center //и вертикально
        ) {
//            if (openDialog.value)
//                MakeAlertDialog(
//                curArm.toString(),
//                openDialog,
//                degsForLegs[curArm]
//            ) { x -> //ф-ия обратного вызова для запоминания угла
//                degsForLegs[curArm] = x
//            } //для вызова окна с нужной leg
            DropdownDemo(bltList){ x-> //лямбда для ф-ии обратного вызова
                curDeviceName=x
                if (curDeviceName!=""){
                    val curDevice: BluetoothDevice = bltWork.getDeviceByName(curDeviceName)
                    println("curDevice = ${curDevice}")
                    socketToDevice = bltWork.connectToBluetoothDevice(curDevice)!!
//                    curSerialPort = SerialPort(curComPort)
//                    curSerialPort.openPort()
                }
            }
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
                            val number = getArmNumber(startPointX, quadroPodBody, startPointY)
                            if (curDeviceName != "") writeArmAngleToArduino(
                                bltWork,
                                socketToDevice!!,
                                number,
                                degsForArms[number]
                            )
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
                            val newAct = Intent(mContext, SecondActivity::class.java) //описан ниже
                            newAct.putExtra("angle", degsForLegs[number])
                            newAct.putExtra("legNumber", number)
                            mContext.startActivity(newAct)
                        }
                    )
                }
            ) {
//                val canvasQuadrantSize = size / 2F
                try {
                    println("screen width = ${size.width.toInt()}, screen height = ${size.height.toInt()}")

                    drawImage(
                        image = quadroPodBody,
//                        topLeft = Offset(0F, 0F),
                        dstSize = IntSize(size.width.toInt(), size.height.toInt())
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
                        rotatePoints[0],
                        degsForArms[0]
                    ){x-> degsForArms[0]=x }
//                    armRotate(0F,0F,arm1, startPointX, startPointY, offsetX, offsetY, rotatePoints)
//                    val arm2 = arms[1]
////                    val x0ForArm2 = rotatePoints[1].first
////                    val y0ForArm2 = rotatePoints[1].second-rotatePoints[0].second
//                    val y0ForArm2 = rotatePoints[1].second - 80 //позиционируем вторую лапу
//                    armRotate(
//                        2,
//                        0F,
//                        y0ForArm2.toFloat(),
//                        arm2,
//                        startPointXArray[1],
//                        startPointYArray[1],
//                        offsetXArray[1],
//                        offsetYArray[1],
//                        rotatePoints[1],
//                        degsForArms[1]
//                    ){x-> degsForArms[1]=x }
//                    val arm3 = arms[2]
//                    val x0ForArm3 = rotatePoints[2].first - 40 //позиционируем третью лапу
//                    armRotate(
//                        3,
//                        x0ForArm3.toFloat(),
//                        7F,
//                        arm3,
//                        startPointXArray[2],
//                        startPointYArray[2],
//                        offsetXArray[2],
//                        offsetYArray[2],
//                        rotatePoints[2],
//                        degsForArms[2]
//                    ){x-> degsForArms[2]=x }
//                    val arm4 = arms[3]
//                    val x0ForArm4 = rotatePoints[3].first - 40
//                    val y0ForArm4 = rotatePoints[3].second - 55 //позиционируем четвертую лапу
//                    armRotate(
//                        4,
//                        x0ForArm4.toFloat(),
//                        y0ForArm4.toFloat(),
//                        arm4,
//                        startPointXArray[3],
//                        startPointYArray[3],
//                        offsetXArray[3],
//                        offsetYArray[3],
//                        rotatePoints[3],
//                        degsForArms[3]
//                    ){x-> degsForArms[3]=x }
                } catch (e: NullPointerException) {
//                    Toast.makeText(applicationContext,"No image", Toast.LENGTH_LONG).show()
                    println("No image")
                }
            }
        }
//        }
    }

//    data class ConnectedBluetoothDevices(
//        val a2dpDevices: List<BluetoothDevice>,
//        val gattDevices: List<BluetoothDevice>,
//        val gattServerDevices: List<BluetoothDevice>,
//        val headsetDevices: List<BluetoothDevice>,
//        val sapDevices: List<BluetoothDevice>,
//    )
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
//    @Composable
//    fun MakeAlertDialog(
//        curArm: String,
//        openDialog: MutableState<Boolean>,
//        degsInLeg: Float,
////    startPointX: Float,
////    startPointY: Float,
//        onUpdate: (x: Float) -> Unit
//    ) { //показываем окно с нужным leg для его поворота
////    var degsInDialog = 0f
//        var degs by remember { mutableStateOf(0f) }
//        degs = degsInLeg
//
//        AlertDialog( //todo переделать в отдельное активити
//            onDismissRequest = { //действия при закрытии окна
//                openDialog.value = false
//                onUpdate(degs)
//                println("Exit")
//            },
//            modifier = Modifier.fillMaxSize(),
//            title = { Text(text = curArm) }, //заголовок окна
//            text = { //внутренняя часть окна
//                val backImage = BitmapFactory.decodeResource(
//                    LocalContext.current.resources,
//                    R.drawable.back
//                )
//
//                var legBody: Bitmap? = null
//                val res = LocalContext.current.resources
//                legBody = curLegBody(curArm.toInt(), res)
//                val pixMap = legBody
//                var rotatePoint: Pair<Int, Int>? = null
//                for (x in 11 until legBody!!.width) { //в циклах ищем зеленые точки, чтоб их добавить к массиву точек поворота
//                    for (y in 11 until legBody.height) {
//                        if (pixMap!![x, y].green in (200..255) && pixMap[x, y].red < 125 && pixMap[x, y].blue < 255) {
////                        println("found green on leg${curArm.toInt() + 1} at $x $y")
//                            rotatePoint = Pair(x, y)
//                        }
//                    }
//                }
//                var offsetX by remember { mutableStateOf(0f) }
//                var offsetY by remember { mutableStateOf(0f) }
//                var startPointX by remember { mutableStateOf(0f) }
//                var startPointY by remember { mutableStateOf(0f) }
//
//                var leg: Bitmap? = curLeg(curArm.toInt(), res)
//
//                Canvas(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .pointerInput(Unit) {
//                            detectDragGestures(
//                                onDragStart = { touch ->
////                            println("\nStart of the interaction is x=${touch.x} y=${touch.y}")
////                                onUpdate(touch.x, touch.y)
//                                    startPointX = touch.x
//                                    startPointY = touch.y
//
//                                    offsetX = 0F //сбрасываем оффсеты, чтобы нормально двигать ногу
//                                    offsetY = 0F
//                                    var number = 0
//                                },
//                                onDrag = { change, dragAmount ->
//                                    change.consume()
////                            println("in listener x    = ${dragAmount.x}  y = ${dragAmount.y}  ")
////                            println("arm1RotatePointX = $arm1RotatePointX arm1RotatePointY = $arm1RotatePointY" )
//                                    offsetX += dragAmount.x
//                                    offsetY += dragAmount.y
//                                    degs = angle(
//                                        rotatePoint!!.first.toFloat(),
//                                        rotatePoint.second.toFloat(),
//                                        startPointX,
//                                        startPointY,
//                                        offsetX,
//                                        offsetY
//                                    )
////                                println("angle = $degs")
//                                },
//                                onDragEnd = { },
//                            )
//                        }
//                ) {
//                    var rotatePointLeg: Pair<Int, Int>? = null
//                    val pixMapForLeg = leg
//                    for (x in 11 until leg!!.width) { //в циклах ищем зеленые точки, чтоб их добавить к массиву точек поворота
//                        for (y in 11 until leg.height) {
//                            if ((pixMapForLeg!![x, y].green in (200..255) && curArm.toInt() != 0)
//                                || (curArm.toInt() == 0 && pixMapForLeg[x, y].green in (200..255) && pixMapForLeg[x, y].red < 85 && pixMapForLeg[x, y].blue < 85)
//                            ) {
////                        println("found green on leg${curArm.toInt() + 1} at $x $y")
//                                rotatePointLeg = Pair(x, y)
//                            }
//                        }
//                    }
////                println("for leg pair.x = ${rotatePointLeg?.first}, pair.y = ${rotatePointLeg?.second}")
////                val degs = angle(rotatePoint!!.first.toFloat(), rotatePoint.second.toFloat(), startPointX, startPointY, offsetX, offsetY)
////                println("angle for leg = $degs")
//                    try {
//                        drawImage(
//                            image = backImage.asImageBitmap(),
//                            topLeft = Offset(0F, 0F)
//                        )
////                    println("curArm = $curArm")
//                        if (curArm.toInt() == 0 || curArm.toInt() == 2) {
//                            legRotate(curArm.toInt(), degs, leg.asImageBitmap(), rotatePointLeg!!, rotatePoint!!)
//                            drawImage(
//                                image = legBody!!.asImageBitmap(),
//                                topLeft = Offset(0F, 0F)
//                            )
//                        } else {
//                            drawImage(
//                                image = legBody!!.asImageBitmap(),
//                                topLeft = Offset(0F, 0F)
//                            )
//                            legRotate(curArm.toInt(), degs, leg.asImageBitmap(), rotatePointLeg!!, rotatePoint!!)
//                        }
//                    } catch (e: NullPointerException) {
////                    Toast.makeText(applicationContext,"No image", Toast.LENGTH_LONG).show()
//                        println("No image")
//                    }
////                val degs = angle(armRotatePointX, armRotatePointY + y0, startPointX, startPointY, offsetX, offsetY)
//                }
//            },
//            confirmButton = { //кнопка Ok, которая будет закрывать окно
//                Button(onClick = {
//                    openDialog.value = false
//                    onUpdate(degs)
//                    println("Ok pressed")
//                })
//                { Text(text = "OK") }
//            }
//        )
//    }

//    @Composable
    private fun curLeg(legNumber: Int, res: Resources): Bitmap? {
        var leg: Bitmap? = null
        when (legNumber){
            0-> leg = BitmapFactory.decodeResource(
                res,
                R.drawable.leg1
            )
            1-> leg = BitmapFactory.decodeResource(
                res,
                R.drawable.leg2
            )
            2-> leg = BitmapFactory.decodeResource(
                res,
                R.drawable.leg3
            )
            3-> leg = BitmapFactory.decodeResource(
                res,
                R.drawable.leg4
            )
        }
        return leg
    }

//    @Composable
//    private fun curLegBody(legNumber: Int, res: Resources): Bitmap? {
//        var leg: Bitmap? = null
//        when (legNumber){
//            0-> leg = BitmapFactory.decodeResource(
//                res,
//                R.drawable.leg1_body_
//            )
//            1-> leg = BitmapFactory.decodeResource(
//                res,
//                R.drawable.leg2_body_
//            )
//            2-> leg = BitmapFactory.decodeResource(
//                res,
//                R.drawable.leg3_body_
//            )
//            3-> leg = BitmapFactory.decodeResource(
//                res,
//                R.drawable.leg4_body_
//            )
//        }
//        return leg
//    }

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

    fun loadArms(res: Resources): Array<ImageBitmap> { //для загрузки изображений ног робота (плечи)
        var armImagesArray = arrayOf<ImageBitmap>()
        var arm = BitmapFactory.decodeResource(
            res,
            R.drawable.arm1
        )
        armImagesArray = armImagesArray.plus(arm.asImageBitmap())
        arm = BitmapFactory.decodeResource(
            res,
            R.drawable.arm2
        )
        armImagesArray = armImagesArray.plus(arm.asImageBitmap())
        arm = BitmapFactory.decodeResource(
            res,
            R.drawable.arm3
        )
        armImagesArray = armImagesArray.plus(arm.asImageBitmap())
        arm = BitmapFactory.decodeResource(
            res,
            R.drawable.arm4
        )
        armImagesArray = armImagesArray.plus(arm.asImageBitmap())

        return armImagesArray
    }
}

@Composable
fun DropdownDemo(itemsInitial: List<String>, onUpdate: (x: String) -> Unit) { //комбобокс для выбора компорта для подключения к Arduino
    var expanded by remember { mutableStateOf(false) }
//    val items = listOf("com1", "com2", "com3")
//    val disabledValue = "B"
    var items = remember { mutableStateListOf<String>() }
    itemsInitial.forEach {
        if (!items.contains(it))items.add(it)
    }
    var selectedIndex by remember { mutableStateOf(-1) }
    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
        Text( //заголовок комбобокса
            if (selectedIndex<0) "Выберите устройство: ▼" //если еще ничего не выбрано
            else items[selectedIndex], //если выбрано
            modifier = Modifier.clickable(onClick = { //при нажатии на текст раскрываем комбобокс
//                val tempPortList = SerialPortList.getPortNames().toList() //получаем активные порты
//                println("SerialPortList = $tempPortList")
//                tempPortList.forEach {//добавляем новые порты к списку
//                    if (!items.contains(it))items.add(it)
//                }
//                items.forEach{//убираем отключенные порты
//                    if (!tempPortList.contains(it)) {
////                        println("$it not in SerialPortList")
//                        items.remove(it)
//                    }
//                }
//                val bltList =
                expanded = true
            })
        )
        DropdownMenu( //сам выпадающий список для комбобокса
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            items.forEachIndexed { index, s -> //заполняем элементы выпадающего списка
                DropdownMenuItem(
//                   Text(text = s),
                    text = {Text(text = s )},
                    onClick = { //обработка нажатия на порт
                        selectedIndex = index
                        expanded = false
                        onUpdate(s)
                        println("selected = $s")
                    }
                )
//                {
////                    val disabledText = if (s == disabledValue) {
////                        " (Disabled)"
////                    } else {
////                        ""
////                    }
//                    Text(text = s )
//                }
            }
        }
    }
}