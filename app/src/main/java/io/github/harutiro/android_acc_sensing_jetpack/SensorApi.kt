package io.github.harutiro.android_acc_sensing_jetpack

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 加速度センサーの値を取得し、Jetpack Compose にデータを渡すクラス
 *
 * @param context アプリケーションのコンテキスト
 */
class SensorApi(context: Context) : SensorEventListener {

    // センサーマネージャーを取得（Android のセンサーを管理するためのクラス）
    private var sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    // 加速度センサーを取得（デバイスによってはセンサーが存在しない場合もある）
    private var accSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

    // センサーの値を保持するための `StateFlow`（Jetpack Compose で値をリアルタイムに監視するために使用）
    private val _sensorValues = MutableStateFlow(Triple(0f, 0f, 0f)) // 初期値 (0,0,0)
    val sensorValues = _sensorValues.asStateFlow() // 外部からは `asStateFlow` で読み取り専用にする

    /**
     * センサーの値が変化したときに呼ばれる関数
     *
     * @param event センサーイベント（新しい値が入っている）
     */
    override fun onSensorChanged(event: SensorEvent?) {
        // 受け取ったイベントが加速度センサーのものであるかチェック
        if (event?.sensor?.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            // 加速度センサーの X, Y, Z 軸の値を取得
            val sensorX = event.values[0] // X 軸の加速度
            val sensorY = event.values[1] // Y 軸の加速度
            val sensorZ = event.values[2] // Z 軸の加速度

            // デバッグログに値を出力（開発時に値を確認するため）
            val strTmp = """加速度センサー
                         X: $sensorX
                         Y: $sensorY
                         Z: $sensorZ"""
            Log.d("SensorApi", strTmp)

            // Flow に新しいセンサーの値を更新（Jetpack Compose にリアルタイムで反映される）
            _sensorValues.value = Triple(sensorX, sensorY, sensorZ)
        }
    }

    /**
     * センサーの精度が変化したときに呼ばれる関数
     * ※ 今回は特に使用しないが、インターフェースの実装が必要なため空のまま残す
     *
     * @param sensor センサーのオブジェクト
     * @param accuracy 新しい精度のレベル（センサーの状態が変化した際に通知される）
     */
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // 今回は特に処理しない
    }

    /**
     * センサーの監視を開始する（加速度センサーの値を取得し始める）
     * `registerListener` を呼ぶことで `onSensorChanged` が定期的に呼ばれる
     */
    fun register() {
        accSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    /**
     * センサーの監視を停止する（アプリが閉じたり、画面が切り替わるときに呼ぶ）
     * センサーの監視を停止しないとバッテリー消費が増えてしまう
     */
    fun unregister() {
        sensorManager.unregisterListener(this)
    }
}