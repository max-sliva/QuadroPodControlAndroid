package com.example.quadropodcontrol

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.util.UUID

class BluetoothWork(localContext: Context) {
    private var context: Context
    private var bltList = listOf<String>()
    private var pairedDevices: Set<BluetoothDevice>? = null

    init{
        context = localContext

    }

    fun getBluetoothDevices( onUpdate: (list: List<String>) -> Unit): Set<BluetoothDevice>? {

        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter
        if (!bluetoothAdapter.isEnabled) {
            println("Bluetooth is not enabled")
        }
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            println("Should Requesting Bluetooth permission")
//            return emptyList()
        }
        pairedDevices = bluetoothAdapter.bondedDevices
//        println("---!! set of devices size = ${pairedDevices?.size} !!!-----")
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address // MAC address
//            println("blt device = $deviceName")
            bltList = bltList.plus(deviceName)
        }
        onUpdate(bltList)
        return pairedDevices
    }

    fun connectToBluetoothDevice(device: BluetoothDevice): BluetoothSocket? {
        // Check if the device is already connected
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            println("Should Requesting Bluetooth permission")
//            return emptyList()
        }
        var socket: BluetoothSocket? = null
//        if (device.bondState!= BluetoothDevice.BOND_BONDED) {
            // Create a BluetoothSocket for the device
            socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))

            // Attempt to connect to the device
            socket.connect()
            println("device is connected")
            // Do something with the socket, such as send or receive data
//        }
        return socket
    }
    fun sendDataToBluetoothDevice(socket: BluetoothSocket, data: String) {
        try {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                println("Should Requesting Bluetooth permission")
//            return emptyList()
            }
            // Get the BluetoothSocket for the device
//            val socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
//            socket.connect()
            // Convert the string to bytes
            val bytes = data.toByteArray()
            // Send the bytes to the device
            val outputStream = socket.outputStream
            outputStream.write(bytes)
            // Close the socket
//            socket.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getDeviceList(): List<String> = bltList
    fun getDeviceByName(curDeviceName: String): BluetoothDevice {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            println("Should Requesting Bluetooth permission")
//            return emptyList()
        }
        val curDevice = pairedDevices?.filter {
            it.name==curDeviceName
        }?.first()

        return curDevice!!
    }

}