package io.github.harutiro.android_acc_sensing_jetpack


import android.content.Context
import android.os.Environment
import java.io.BufferedWriter
import java.io.FileWriter
import java.io.PrintWriter

/**
 * **ログデータを外部ストレージのファイルに保存するクラス**
 *
 * このクラスは、加速度データなどのログを CSV 形式で保存するために使用されます。
 * デバイスの **内部ストレージの "Documents" フォルダ** にログファイルを作成し、データを記録します。
 *
 * @param context アプリのコンテキスト（Androidのシステム情報へのアクセスに必要）
 * @param fileName 保存するファイルの名前（拡張子なし）
 */
class OtherFileStorage(val context: Context, val fileName: String) {

    // **ファイルの書き込みモード**
    // `true` の場合は既存のファイルに追記する（上書きしない）
    // `false` の場合は新規作成し、古いデータを上書きする
    val fileAppend: Boolean = true

    // **ファイルの拡張子**
    // CSV 形式で保存するため、拡張子を `.csv` に設定
    val extension: String = ".csv"

    // **ファイルの保存先パス**
    // `getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)` を使って
    // 内部ストレージの "Documents" フォルダにファイルを作成
    val filePath: String =
        context.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            .toString().plus("/").plus(fileName).plus(extension)

    // **基準時刻**
    // ログのタイムスタンプをミリ秒単位で記録するために、基準時刻を取得
    val baseTime: Long = System.currentTimeMillis()

    // **データの次元数**
    // 1次元（x）、2次元（x, y）、3次元（x, y, z）など、記録するデータの次元数を設定
    val dimension: Int = 3

    // **初期処理**
    // クラスのインスタンスが作成されたら、CSVの1行目（ヘッダー行）を記録する
    init {
        writeText(firstLog(dimension), filePath)
    }

    /**
     * **ログデータをファイルに記録する関数**
     *
     * @param text 記録するデータ（例："X, Y, Z" などの加速度データ）
     */
    fun doLog(text: String) {
        // 経過時間（ミリ秒単位）を計算
        val elapsedMillis = System.currentTimeMillis() - baseTime

        // `elapsedMillis`（経過時間）と `text`（ログデータ）をカンマ区切りで記録
        writeText("$elapsedMillis,$text", filePath)
    }

    /**
     * **CSVの1行目（ヘッダー行）を作成する関数**
     *
     * 例:
     * - 1次元: `2025-02-26T12:00:00.000Z,x`
     * - 2次元: `2025-02-26T12:00:00.000Z,x,y`
     * - 3次元: `2025-02-26T12:00:00.000Z,x,y,z`
     *
     * @param dimension 記録するデータの次元数
     * @return CSVの1行目の文字列
     */
    private fun firstLog(dimension: Int): String {
        return when (dimension) {
            1 -> baseTime.toString().plus(",x")  // 1次元データ（x軸のみ）
            2 -> baseTime.toString().plus(",x,y") // 2次元データ（x, y）
            3 -> baseTime.toString().plus(",x,y,z") // 3次元データ（x, y, z）
            else -> {
                // **4次元以上の場合**
                // "timestamp,0,1,2,3,4..." のように可変次元のヘッダーを作成
                var result: String = baseTime.toString()
                for (i in 0 until dimension) {
                    result = result.plus(",").plus(i)
                }
                result
            }
        }
    }

    /**
     * **ログデータを外部ストレージのファイルに書き込む関数**
     *
     * @param text 書き込む文字列（CSVフォーマットの1行）
     * @param path ファイルの保存先パス
     */
    private fun writeText(text: String, path: String) {
        // `FileWriter` を使用して指定されたファイルを開く
        val file = FileWriter(path, fileAppend) // `fileAppend = true` なら追記、`false` なら上書き

        // `BufferedWriter` で書き込みをバッファリングして高速化
        val printWriter = PrintWriter(BufferedWriter(file))

        // テキストを1行書き込む
        printWriter.println(text)

        // **ファイルを閉じる（リソースの開放）**
        printWriter.close()
    }
}