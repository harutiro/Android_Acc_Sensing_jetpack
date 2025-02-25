package io.github.harutiro.android_acc_sensing_jetpack

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SensorApi(context: Context) : SensorEventListener {

    private var sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var accSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

    // Flow を使ってデータを管理
    private val _sensorValues = MutableStateFlow(Triple(0f, 0f, 0f))
    val sensorValues = _sensorValues.asStateFlow()

    /**
     * センサーの値が変化したときに呼ばれるコールバック関数。
     *
     * @param event センサーイベント
     */
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            val sensorX = event.values[0]
            val sensorY = event.values[1]
            val sensorZ = event.values[2]

            val strTmp = """加速度センサー
                         X: $sensorX
                         Y: $sensorY
                         Z: $sensorZ"""

            _sensorValues.value = Triple(sensorX, sensorY, sensorZ)

            Log.d("SensorApi", strTmp)
        }
    }

    /**
     * センサーの精度が変化したときに呼ばれるコールバック関数。
     *
     * @param sensor センサーオブジェクト
     * @param accuracy 精度の値
     */
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    fun register() {
        accSensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }

    fun unregister() {
        sensorManager.unregisterListener(this)
    }
}