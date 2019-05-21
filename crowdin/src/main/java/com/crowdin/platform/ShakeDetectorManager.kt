package com.crowdin.platform

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.widget.Toast
import com.crowdin.platform.util.ShakeDetector

internal class ShakeDetectorManager {

    private lateinit var shakeDetector: ShakeDetector
    private var mSensorManager: SensorManager? = null

    fun registerShakeDetector(context: Context) {
        mSensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val mAccelerometer = mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        shakeDetector = ShakeDetector()
        val shakeListener = object : ShakeDetector.OnShakeListener {

            override fun onShake(count: Int) {
                Crowdin.forceUpdate(context)
                Toast.makeText(context, "Shake: force update", Toast.LENGTH_SHORT).show()
            }
        }
        shakeDetector.setOnShakeListener(shakeListener)
        mSensorManager?.registerListener(shakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    fun unregisterShakeDetector() {
        mSensorManager?.unregisterListener(shakeDetector)
    }
}
