import android.bluetooth.BluetoothSocket
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.example.quadropodcontrol.BluetoothWork
import com.example.quadropodcontrol.R
import kotlin.math.atan

fun degsForLeg(degs: Float, curArm: Int) = degs * (if(curArm==0 || curArm==1) -1 else 1)

fun curLegBody(legNumber: Int, res: Resources): Bitmap? {
    var leg: Bitmap? = null
    when (legNumber){
        0-> leg = BitmapFactory.decodeResource(
            res,
            R.drawable.leg1_body_
        )
        1-> leg = BitmapFactory.decodeResource(
            res,
            R.drawable.leg2_body_
        )
        2-> leg = BitmapFactory.decodeResource(
            res,
            R.drawable.leg3_body_
        )
        3-> leg = BitmapFactory.decodeResource(
            res,
            R.drawable.leg4_body_
        )
    }
    return leg
}

fun curLeg(legNumber: Int, res: Resources): Bitmap? {
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

fun DrawScope.armRotate(
    ratio: Float,
    armNumber: Int,
    x0: Float,
    y0: Float,
    arm: ImageBitmap,
    startPointX: Float,
    startPointY: Float,
    offsetX: Float,
    offsetY: Float,
//    rotatePoints: Array<Pair<Int, Int>>
    rotatePoints: Pair<Int, Int>,
//    curSerialPort: SerialPort,
    curAngleForArm: Int,
    onAngleChanged:(x: Int) -> Unit
) {
    val armRotatePointX = arm.width.toFloat()/ratio
    val armRotatePointY = (arm.height / 2).toFloat()/ratio
    val degs = angle(armRotatePointX, armRotatePointY + y0, startPointX, startPointY, offsetX, offsetY)
//    println(" angle = $degs ")
    val angleToComPort = angleForServoArm(degs, armNumber)
//  todo разобраться с нумерацией сервов и сделать плавное изменение угла
    if (curAngleForArm!=angleToComPort) {
        println("for arm#$armNumber curAngleForArm = $curAngleForArm, angleToComPort = $angleToComPort")
        onAngleChanged(angleToComPort)
//        if (curSerialPort.portName != "") writeAngleToComPort(curSerialPort, armNumber, angleToComPort)
    }
    if (armNumber == 1) { //для arm1
//        angleForServoArm(degs, armNumber)
        if (degs <= 65 && degs > -85 && startPointX + offsetX < armRotatePointX)
            rotate(degrees = -degs, Offset(rotatePoints.first.toFloat()/ ratio, rotatePoints.second.toFloat()/ ratio)) {
                drawImage(
                    image = arm,
                    dstSize = IntSize((arm.width/ratio).toInt(), (arm.height/ratio).toInt()),
//                    topLeft = Offset(x0, y0)
                    dstOffset = IntOffset(x = x0.toInt(), y = y0.toInt())
                )
            } else
//                        if (degs >=60)
            if (startPointY + offsetY > armRotatePointY)
                rotate(degrees = -65F, Offset(rotatePoints.first.toFloat()/ ratio, rotatePoints.second.toFloat()/ ratio)) {
                    drawImage(
                        image = arm,
//                        topLeft = Offset(x0, y0)
                        dstSize = IntSize((arm.width/ratio).toInt(), (arm.height/ratio).toInt()),
                        dstOffset = IntOffset(x = x0.toInt(), y = y0.toInt())
                    )
                }
            else
                if (startPointY + offsetY < armRotatePointY)
                    rotate(degrees = 85F, Offset(rotatePoints.first.toFloat()/ ratio, rotatePoints.second.toFloat()/ ratio)) {
                        drawImage(
                            image = arm,
                            dstSize = IntSize((arm.width/ratio).toInt(), (arm.height/ratio).toInt()),
                            dstOffset = IntOffset(x = x0.toInt(), y = y0.toInt())
                        )
                    }
    } else if (armNumber == 2) {
//        println("startPointY = $startPointY, offsetY = $offsetY, arm1RotatePointY +y0= ${armRotatePointY + y0}")
        if (degs >= -65 && degs <= 85 && startPointX + offsetX < armRotatePointX)
            rotate(degrees = -degs, Offset(rotatePoints.first.toFloat()/ ratio, rotatePoints.second.toFloat()/ ratio)) {
                drawImage(
                    image = arm,
                    dstSize = IntSize((arm.width/ratio).toInt(), (arm.height/ratio).toInt()),
                    dstOffset = IntOffset(x = x0.toInt(), y = y0.toInt())
                )
            }
        else if ((startPointY + offsetY) < (armRotatePointY + y0)) {
            rotate(degrees = 65F, Offset(rotatePoints.first.toFloat()/ ratio, rotatePoints.second.toFloat()/ ratio)) {
                drawImage(
                    image = arm,
                    dstSize = IntSize((arm.width/ratio).toInt(), (arm.height/ratio).toInt()),
                    dstOffset = IntOffset(x = x0.toInt(), y = y0.toInt())
                )
            }
        } else if ((startPointY + offsetY) > (armRotatePointY + y0))
            rotate(degrees = -85F, Offset(rotatePoints.first.toFloat()/ ratio, rotatePoints.second.toFloat()/ ratio)) {
                drawImage(
                    image = arm,
                    dstSize = IntSize((arm.width/ratio).toInt(), (arm.height/ratio).toInt()),
                    dstOffset = IntOffset(x = x0.toInt(), y = y0.toInt())
                )
            }
    } else if (armNumber == 3) {
//        println("in arm3 startPointY = $startPointY, offsetY = $offsetY, arm1RotatePointY = $armRotatePointY y0 = $y0")
//        println("in arm3 degs = $degs")
        if (degs <= 60 && degs >= -85 && startPointX + offsetX > armRotatePointX || degs == 0F) {
//            println("between")
            rotate(degrees = degs, Offset(rotatePoints.first.toFloat()/ ratio, rotatePoints.second.toFloat()/ ratio)) {
                drawImage(
                    image = arm,
//                    topLeft = Offset(x0, y0),
                    dstSize = IntSize((arm.width/ratio).toInt(), (arm.height/ratio).toInt()),
                    dstOffset = IntOffset(x = x0.toInt(), y = y0.toInt())
                )
            }
        } else if ((startPointY + offsetY) > (armRotatePointY)) {
//            println("up")
            rotate(degrees = 60F, Offset(rotatePoints.first.toFloat()/ ratio, rotatePoints.second.toFloat()/ ratio)) {
                drawImage(
                    image = arm,
                    dstSize = IntSize((arm.width/ratio).toInt(), (arm.height/ratio).toInt()),
                    dstOffset = IntOffset(x = x0.toInt(), y = y0.toInt())
                )
            }
        } else if ((startPointY + offsetY) < (armRotatePointY)) {
//            println("down")
            rotate(degrees = -85F, Offset(rotatePoints.first.toFloat()/ ratio, rotatePoints.second.toFloat()/ ratio)) {
                drawImage(
                    image = arm,
                    dstSize = IntSize((arm.width/ratio).toInt(), (arm.height/ratio).toInt()),
                    dstOffset = IntOffset(x = x0.toInt(), y = y0.toInt())
                )
            }
        }
    } else if (armNumber == 4) {
//        println("in arm4 startPointY = $startPointY, offsetY = $offsetY, arm1RotatePointY = $armRotatePointY y0 = $y0")
//        println("in arm4 degs = $degs")
        if (degs <= 85 && degs >= -60 && startPointX + offsetX > armRotatePointX || degs == 0F) {
            rotate(degrees = degs, Offset(rotatePoints.first.toFloat()/ ratio, rotatePoints.second.toFloat()/ ratio)) {
                drawImage(
                    image = arm,
                    dstSize = IntSize((arm.width/ratio).toInt(), (arm.height/ratio).toInt()),
                    dstOffset = IntOffset(x = x0.toInt(), y = y0.toInt())
                )
            }
        } else if ((startPointY + offsetY) < (armRotatePointY + y0)) {
            rotate(degrees = -60F, Offset(rotatePoints.first.toFloat()/ ratio, rotatePoints.second.toFloat()/ ratio)) {
                drawImage(
                    image = arm,
                    dstSize = IntSize((arm.width/ratio).toInt(), (arm.height/ratio).toInt()),
                    dstOffset = IntOffset(x = x0.toInt(), y = y0.toInt())
                )
            }
        } else if ((startPointY + offsetY) > (armRotatePointY + y0))
            rotate(degrees = 85F, Offset(rotatePoints.first.toFloat()/ ratio, rotatePoints.second.toFloat()/ ratio)) {
                drawImage(
                    image = arm,
                    dstSize = IntSize((arm.width/ratio).toInt(), (arm.height/ratio).toInt()),
                    dstOffset = IntOffset(x = x0.toInt(), y = y0.toInt())
                )
            }
    }
}

fun DrawScope.legRotate(
    curArm: Int,
    degs: Float,
    leg: ImageBitmap,
    rotatePointLeg: Pair<Int, Int>,
    rotatePoint: Pair<Int, Int>
) {
    if (degs >=-89F)
        rotate(degrees = degsForLeg(degs, curArm), Offset(rotatePoint!!.first.toFloat(), rotatePoint.second.toFloat())) {
            drawImage(
                image = leg,
                topLeft = Offset(
                    (rotatePoint!!.first - rotatePointLeg!!.first).toFloat(),
                    (rotatePoint.second - rotatePointLeg.second).toFloat()
                )
            )
    } else
//        if()
        rotate(degrees = degsForLeg(degs, curArm), Offset(rotatePoint!!.first.toFloat(), rotatePoint.second.toFloat())) {
            drawImage(
                image = leg,
                topLeft = Offset(
                    (rotatePoint!!.first - rotatePointLeg!!.first).toFloat(),
                    (rotatePoint.second - rotatePointLeg.second).toFloat()
                )
            )
        }
}

fun angleForServoArm(degs: Float, arm: Int): Int {
    //arm1:  65  -80   servo: down 165, up 20
//    arm2: -65 80    servo:
//    arm3:           servo: down 20  up 165
    var angle = 0
//    if (arm in 0..1) {
//        angle = convert(degs.toInt()+85, IntRange(0, 150), IntRange(30, 180))
//    }
    if (arm==1 ) angle = (degs+100).toInt()
    if (arm==2) angle = (degs+100).toInt()-40
    if (arm==3) angle = (-degs+100).toInt()-20
    if (arm==4) angle = (-degs+100).toInt()+20
    println("arm$arm to servo = $angle")
    return angle
}

fun angleForServoLeg(degs: Float, leg: Int): Int {
    var angle = 0

    return angle
}

fun sendToArduino(arm: Int, angle: Int){
 //https://github.com/java-native/jssc/wiki/examples
}

fun writeArmAngleToArduino(bltWork: BluetoothWork, curSocket: BluetoothSocket, armNumber: Int, angleToComPort:  Int, isArm: Boolean=true) {
    println("trying to send angle = $angleToComPort for ${if (isArm) "arm" else "leg"}=$armNumber")
    if (curSocket.isConnected) {
        var armNumberToSend = armNumber
//        curComPort.setParams(9600, 8, 1, 0)
//        if (armNumber == 3) armNumberToSend = 4
//        if (armNumber == 4) armNumberToSend = 3
//        curComPort.writeString("${(armNumberToSend-1)*2}-$angleToComPort\n")
        if (armNumber == 2) armNumberToSend = 3
        if (armNumber == 3) armNumberToSend = 2
//        bltWork.sendDataToBluetoothDevice(curSocket, "")
        if (isArm) bltWork.sendDataToBluetoothDevice(curSocket,"${armNumberToSend*2}-$angleToComPort\n")
        else bltWork.sendDataToBluetoothDevice(curSocket,"${armNumberToSend*2+1}-$angleToComPort\n")
    }
}