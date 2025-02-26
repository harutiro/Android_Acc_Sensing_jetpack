package io.github.harutiro.android_acc_sensing_jetpack

import kotlin.math.sqrt

/**
 * **転倒検知クラス**
 *
 * このクラスは、加速度データをもとに転倒を検知する機能を提供します。
 * 加速度の大きさ（ノルム）が **閾値（しきい値）** を超えた場合、転倒と判断します。
 *
 * **検出の流れ:**
 * 1. X, Y, Z 軸の加速度データを受け取る
 * 2. ノルム（ベクトルの大きさ）を計算する
 * 3. 過去のデータを考慮し、ノイズを除去する
 * 4. 一定の基準を超えた場合、転倒を検出する
 */
class FallDetection {

    // **過去の加速度ノルムのデータを保存するリスト**
    private val normData = mutableListOf<Double>()

    // **転倒を判定するための閾値（しきい値）**
    // この値を超えると転倒とみなす
    private val threshold: Double = 5.0

    /**
     * **加速度データを追加し、転倒の有無を判定する**
     *
     * @param x X軸方向の加速度
     * @param y Y軸方向の加速度
     * @param z Z軸方向の加速度
     * @return 転倒が検知された場合は `true`、そうでない場合は `false`
     */
    fun addAccelerationData(x: Double, y: Double, z: Double): Boolean {
        // **1. ノルム（加速度の大きさ）を計算**
        val norm = calculateNorm(x, y, z)

        // **2. ノイズを除去（過去のデータを考慮して滑らかに）**
        val noiseRemovedNorm = noiseRemoval(norm)

        // **3. 計算したノルムをデータリストに追加**
        normData.add(noiseRemovedNorm)

        // **4. 転倒判定**
        // 最新のデータを含む5つのノルムデータを使用して転倒を判定
        return if (normData.size >= 5) {
            norm >= threshold // 閾値を超えた場合、転倒と判断
        } else {
            false // データが十分に集まっていない場合は転倒とみなさない
        }
    }

    /**
     * **ノルム（加速度の大きさ）を計算する**
     *
     * ノルムとは、ベクトル（X, Y, Z）の大きさを表す指標です。
     * 物体の動きの激しさを測るために使用されます。
     *
     * @param x X軸方向の加速度
     * @param y Y軸方向の加速度
     * @param z Z軸方向の加速度
     * @return 計算されたノルムの値（加速度の大きさ）
     */
    private fun calculateNorm(x: Double, y: Double, z: Double): Double {
        return sqrt(x * x + y * y + z * z) // √(x² + y² + z²)
    }

    /**
     * **ノイズ除去処理を行う**
     *
     * 加速度データには、センサーの微細な誤差（ノイズ）が含まれるため、
     * データを滑らかにするために過去のデータを考慮した平均を計算します。
     *
     * @param norm 現在の加速度のノルム（大きさ）
     * @return ノイズ除去後のノルム
     */
    private fun noiseRemoval(norm: Double): Double {
        return if (normData.size >= 5) {
            // **過去5回分のデータの平均を計算してノイズを低減**
            var sum = norm
            for (i in normData.size - 4 until normData.size) {
                sum += normData[i] // 過去のデータを合計
            }
            sum / 5 // 平均を取る
        } else {
            // データが十分に集まっていない場合はそのまま返す
            norm
        }
    }
}
