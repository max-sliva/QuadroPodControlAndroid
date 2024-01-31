package com.example.quadropodcontrol

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.quadropodcontrol.ui.theme.QuadroPodControlTheme
import kotlinx.coroutines.async

class MyLauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val localContext = this
//        GlobalScope.launch{
//            imageWork(localContext)
//        }
        setContent {
            var loading by remember { mutableStateOf(true) } //для обозначения окончания загрузки ресурсов
            val coroutineScope = rememberCoroutineScope()
            LaunchedEffect(key1 = Unit) {
                coroutineScope.async {
//                    withContext(Dispatchers.Default) {
//                        myTimer(1)
//                    }
                    loading = false
                    Toast.makeText(applicationContext, "Timer stopped", Toast.LENGTH_LONG).show()
                }
            }
            println("Timer launched")
            var bltList = listOf<String>() //список имен устройств
            val bltWork:BluetoothWork? = BluetoothWork(LocalContext.current, this)
//        Toast.makeText(LocalContext.current, "get devices", Toast.LENGTH_LONG).show()
            var pairedDevices = remember { mutableSetOf<BluetoothDevice>() }
            pairedDevices = bltWork?.getBluetoothDevices() { list-> bltList=list} as MutableSet<BluetoothDevice>  //сами устройства
            var socketToDevice: BluetoothSocket? by remember { mutableStateOf(null) }
            println("blt devices = ${bltList}")
            var curDeviceName by remember { mutableStateOf("")        }
            var deviceIsChosen by remember { mutableStateOf(false) }
            var checkedState = remember { mutableStateOf(false) }
            QuadroPodControlTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(), //заполняем всё доступное пространство
                        horizontalAlignment = Alignment.CenterHorizontally, //по центру горизонтально
//            verticalArrangement = Arrangement.Center //и вертикально
                    ) {
                        BluetoothDropdownList(bltList){ x-> //лямбда для ф-ии обратного вызова
                            curDeviceName=x
                            if (curDeviceName!=""){
                                val curDevice: BluetoothDevice = bltWork.getDeviceByName(curDeviceName)
                                println("curDevice = ${curDevice}")
                                try {
//                                    bltWork.connectToBluetoothDevice(curDevice)!!
                                    socketToDevice = bltWork.connectToBluetoothDevice(curDevice)!!
                                } catch (e: NullPointerException){
                                    //socketToDevice = null
                                    if (socketToDevice==null)  println("-----!!  device is null  !!------------")
                                    e.printStackTrace()
                                }
                                println("after choosing device")
//                                else
                                if (socketToDevice!=null) {
                                    deviceIsChosen = true
                                    bltWork.setCurrentSocket(socketToDevice!!)
                                }
//                    curSerialPort = SerialPort(curComPort)
//                    curSerialPort.openPort()
                            }
                        }
                        println("between views")
                        if (!checkedState.value) ModeSelection(deviceIsChosen,"Выберите режим:", loading, bltWork, socketToDevice, checkedState) //убрать deviceIsChosen = true при реальном выборе устройства
                        else ModeSelection(true,"Выберите режим:", loading, bltWork, socketToDevice, checkedState) //убрать deviceIsChosen = true при реальном выборе устройства
                    }
                }
            }
        }
    }
}
//@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeSelection(
    deviceIsChosen: Boolean,
    text: String = "Выберите режим:",
    loading: Boolean,
    bltWork: BluetoothWork,
    socketToDevice: BluetoothSocket?,
    checkedState: MutableState<Boolean>
) {
    val mContext = LocalContext.current
//    var checkedState = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Checkbox(
            checked = checkedState.value,
            onCheckedChange = { checkedState.value = it }
        )
        Text(
            text = "$text",
            modifier = Modifier
        )
        Button(
            onClick = {
                val newAct = Intent(mContext, ArmsAndLegsControlActivity::class.java) //описан ниже
//                newAct.putExtra("angle", degsForLegs[number])
//                newAct.putExtra("bltWork", bltWork)
//                newAct.putExtra("socketToDevice", socketToDevice)
                mContext.startActivity(newAct)
            },
            enabled = deviceIsChosen && !loading,
            modifier = Modifier
//                .padding(8.dp)
//                .border(2.dp, Color.Blue,
//                    shape = MaterialTheme.shapes.small)
        ) {
            Text(text = "Управление любой конечностью")
        }
        Button(
            onClick = {
                val newAct = Intent(mContext, RobotMovingActivity::class.java) //описан ниже
//                newAct.putExtra("angle", degsForLegs[number])
//                newAct.putExtra("legNumber", number)
                mContext.startActivity(newAct)
            },
            enabled = deviceIsChosen && !loading,
            modifier = Modifier
//                .padding(8.dp)
//                .border(2.dp, Color.Blue,
//                    shape = MaterialTheme.shapes.small)
        ) {
            Text(text = "Управление перемещением")
        }
//        var loading by remember { mutableStateOf(true) }

//        Button(onClick = { loading = !loading }) {
//            if (!loading) Text("Start loading")
//            else Text("Stop loading")
//        }

        if (loading)
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
//        Button(onClick = { loading = false }, enabled = !loading) {
//            Text("Stop loading")
//        }


    }
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    QuadroPodControlTheme {
//        Greeting(deviceIsChosen, "Android")
//    }
//}