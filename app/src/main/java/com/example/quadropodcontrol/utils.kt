import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.atan

fun degsForLeg(degs: Float, curArm: Int) = degs * (if(curArm==0 || curArm==1) -1 else 1)

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
    armNumber: Int,
    x0: Float,
    y0: Float,
    arm: ImageBitmap,
    startPointX: Float,
    startPointY: Float,
    offsetX: Float,
    offsetY: Float,
//    rotatePoints: Array<Pair<Int, Int>>
    rotatePoints: Pair<Int, Int>
) {
    val armRotatePointX = arm.width.toFloat()
    val armRotatePointY = (arm.height / 2).toFloat()
    val degs = angle(armRotatePointX, armRotatePointY + y0, startPointX, startPointY, offsetX, offsetY)
//    println(" angle = $degs ")
    //ограничиваем поворот
//                    if (degs<=65 || degs>=-90)
//                    if (degs<=65)
    angleForServoArm(degs, armNumber)
    if (armNumber == 1) { //для arm1
//        angleForServoArm(degs, armNumber)
        if (degs <= 65 && degs > -85 && startPointX + offsetX < armRotatePointX)
            rotate(degrees = -degs, Offset(rotatePoints.first.toFloat(), rotatePoints.second.toFloat())) {
                drawImage(
                    image = arm,
                    topLeft = Offset(x0, y0)
                )
            } else
//                        if (degs >=60)
            if (startPointY + offsetY > armRotatePointY)
                rotate(degrees = -65F, Offset(rotatePoints.first.toFloat(), rotatePoints.second.toFloat())) {
                    drawImage(
                        image = arm,
                        topLeft = Offset(x0, y0)
                    )
                }
            else
                if (startPointY + offsetY < armRotatePointY)
                    rotate(degrees = 85F, Offset(rotatePoints.first.toFloat(), rotatePoints.second.toFloat())) {
                        drawImage(
                            image = arm,
                            topLeft = Offset(x0, y0)
                        )
                    }
    } else if (armNumber == 2) {
//        println("startPointY = $startPointY, offsetY = $offsetY, arm1RotatePointY +y0= ${armRotatePointY + y0}")
        if (degs >= -65 && degs <= 85 && startPointX + offsetX < armRotatePointX)
            rotate(degrees = -degs, Offset(rotatePoints.first.toFloat(), rotatePoints.second.toFloat())) {
                drawImage(
                    image = arm,
                    topLeft = Offset(x0, y0)
                )
            }
        else if ((startPointY + offsetY) < (armRotatePointY + y0)) {
            rotate(degrees = 65F, Offset(rotatePoints.first.toFloat(), rotatePoints.second.toFloat())) {
                drawImage(
                    image = arm,
                    topLeft = Offset(x0, y0)
                )
            }
        } else if ((startPointY + offsetY) > (armRotatePointY + y0))
            rotate(degrees = -85F, Offset(rotatePoints.first.toFloat(), rotatePoints.second.toFloat())) {
                drawImage(
                    image = arm,
                    topLeft = Offset(x0, y0)
                )
            }
    } else if (armNumber == 3) {
//        println("in arm3 startPointY = $startPointY, offsetY = $offsetY, arm1RotatePointY = $armRotatePointY y0 = $y0")
//        println("in arm3 degs = $degs")
        if (degs <= 60 && degs >= -85 && startPointX + offsetX > armRotatePointX || degs == 0F) {
//            println("between")
            rotate(degrees = degs, Offset(rotatePoints.first.toFloat(), rotatePoints.second.toFloat())) {
                drawImage(
                    image = arm,
                    topLeft = Offset(x0, y0)
                )
            }
        } else if ((startPointY + offsetY) > (armRotatePointY)) {
//            println("up")
            rotate(degrees = 60F, Offset(rotatePoints.first.toFloat(), rotatePoints.second.toFloat())) {
                drawImage(
                    image = arm,
                    topLeft = Offset(x0, y0)
                )
            }
        } else if ((startPointY + offsetY) < (armRotatePointY)) {
//            println("down")
            rotate(degrees = -85F, Offset(rotatePoints.first.toFloat(), rotatePoints.second.toFloat())) {
                drawImage(
                    image = arm,
                    topLeft = Offset(x0, y0)
                )
            }
        }
    } else if (armNumber == 4) {
//        println("in arm4 startPointY = $startPointY, offsetY = $offsetY, arm1RotatePointY = $armRotatePointY y0 = $y0")
//        println("in arm4 degs = $degs")
        if (degs <= 85 && degs >= -60 && startPointX + offsetX > armRotatePointX || degs == 0F) {
            rotate(degrees = degs, Offset(rotatePoints.first.toFloat(), rotatePoints.second.toFloat())) {
                drawImage(
                    image = arm,
                    topLeft = Offset(x0, y0)
                )
            }
        } else if ((startPointY + offsetY) < (armRotatePointY + y0)) {
            rotate(degrees = -60F, Offset(rotatePoints.first.toFloat(), rotatePoints.second.toFloat())) {
                drawImage(
                    image = arm,
                    topLeft = Offset(x0, y0)
                )
            }
        } else if ((startPointY + offsetY) > (armRotatePointY + y0))
            rotate(degrees = 85F, Offset(rotatePoints.first.toFloat(), rotatePoints.second.toFloat())) {
                drawImage(
                    image = arm,
                    topLeft = Offset(x0, y0)
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