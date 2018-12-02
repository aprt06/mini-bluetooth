package com.example.kunlaratlue.mini_bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.example.kunlaratlue.mini_bluetooth.Bluetooth.BluetoothService
import kotlinx.android.synthetic.main.activity_main.*


//const val MESSAGE_WRITE:Int = 1
const val REQUEST_ENABLE_BT = 1
private const val TAG = "BT_DEBUG"


class MainActivity : AppCompatActivity() {
    private val MACaddr = "00:21:13:03:ED:E1"
    lateinit var mHandler: Handler
    var mmBluetoothService : BluetoothService? = null
    private lateinit var mBluetoothDevice : BluetoothDevice
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        mHandler = object : Handler(Looper.getMainLooper()){
            override fun handleMessage(msg: Message) {
                when(msg.what){
                   // MESSAGE_WRITE ->
                    //Pass along other messages from the UI
                    else -> super.handleMessage(msg)

                }
             }
        }

        initUI()

    }
    fun initBT(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null) {
         }
        if (!mBluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
         }
        mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(MACaddr)
        mmBluetoothService = BluetoothService(mHandler, mBluetoothDevice)




    }
    fun initUI(){
        switch2.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                switch2.setText("ON")
                mmBluetoothService?.write("1")

            }
            else {
                switch2.setText("OFF")
                mmBluetoothService?.write("0")
            }
        }
        button.setOnClickListener {
            mmBluetoothService?.close()

            mmBluetoothService?.let{mmBluetoothService = null}
            Log.e(TAG,"Successfully close service")
        }
        button2.setOnClickListener {
            initBT()
            Log.e(TAG,"Starting service")
        }
    }
}
