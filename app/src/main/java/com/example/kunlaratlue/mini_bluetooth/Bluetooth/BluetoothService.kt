package com.example.kunlaratlue.mini_bluetooth.Bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.util.Log
import java.io.IOException
import java.io.OutputStream
import java.util.*



private const val TAG = "BT_DEBUG"
const val MESSAGE_WRITE:Int = 1
//const val MESSAGE_TOAST:Int = 2

class BluetoothService {


    private var mHandler : Handler
    private var mConnectedThread: ConnectedThread? = null
    private var mConnectThread: ConnectThread? = null
    private var mBluetoothDevice: BluetoothDevice
    private val MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    constructor(mHandler: Handler,mBluetoothDevice: BluetoothDevice){
        this.mHandler = mHandler
        this.mBluetoothDevice = mBluetoothDevice
        initBTManager()
    }


    fun initBTManager(){

        try {
            mConnectThread = ConnectThread(mBluetoothDevice)
            mConnectThread?.start()


        }catch (e:IOException){

            mConnectedThread?.cancel()
         }
    }

    private inner class ConnectThread(mBluetoothDevice: BluetoothDevice) :Thread(){
        private  var  mSocket:BluetoothSocket? = null


        override fun run() {
            // Cancel discovery because it otherwise slows down the connection.
            //mBluetoothAdapter?.cancelDiscovery()

//            mSocket?.use { socket ->
//                // Connect to the remote device through the socket. This call blocks
//                // until it succeeds or throws an exception.
//                socket.connect()
//
//             }
            try{
                mSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID)
                mSocket?.connect()
            }catch (e:IOException){
                Log.e(TAG,"Could not connect  socket",e)
                mSocket = null
            }

            if(mSocket!=null){
                mConnectedThread = ConnectedThread(mSocket!!)


                mConnectedThread?.start()

                Log.e(TAG,"Successfully Connected to Socket")




            }else Log.e(TAG,"BluetoothSocket is null ")


        }

        fun cancel(){
            try{

                mSocket?.close()


             }catch(e: IOException){
                mSocket = null
                Log.e(TAG,"Could not close the client socket",e)
            }
        }


    }
    fun write(s: String){

        val byteArray = s.toByteArray(Charsets.UTF_8)
        mConnectedThread?.write(byteArray)

    }
    fun close(){
        mConnectThread?.interrupt()
        mConnectedThread?.interrupt()
        mConnectThread?.cancel()
        mConnectedThread?.cancel()
        mConnectThread = null
        mConnectedThread = null


     }



    private inner class ConnectedThread(var mSocket: BluetoothSocket?=null):Thread(){

        //private val mmInStream: InputStream = mSocket.inputStream
        private var mmOutStream: OutputStream = mSocket!!.outputStream
        private var mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream

        fun write(bytes:ByteArray){
             try {

                mmOutStream.write(bytes)
                //mmOutStream.write(bytes)

            }catch (e:IOException){
                Log.e(TAG,"Error occurred when sending data",e)

            }

            val writtenMsg = mHandler.obtainMessage(MESSAGE_WRITE,-1,-1,mmBuffer)
            writtenMsg.sendToTarget()
        }

        fun cancel() {
            try {
                mSocket?.close()
                mSocket = null
              } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }

    }
}

