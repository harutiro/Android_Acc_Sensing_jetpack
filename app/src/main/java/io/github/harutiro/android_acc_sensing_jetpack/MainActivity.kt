package io.github.harutiro.android_acc_sensing_jetpack

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.harutiro.android_acc_sensing_jetpack.ui.theme.Android_Acc_Sensing_jetpackTheme
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Android_Acc_Sensing_jetpackTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AccSensingScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AccSensingScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val sensorApi = remember { SensorApi(context) }
    val sensorValues by sensorApi.sensorValues.collectAsState()
    var isRecording by remember { mutableStateOf(false) }
    var otherFileStorage by remember { mutableStateOf<OtherFileStorage?>(null) }

    DisposableEffect(Unit) {
        sensorApi.register()
        onDispose {
            sensorApi.unregister()
        }
    }

    LaunchedEffect(sensorValues){
        if(isRecording){
            otherFileStorage?.doLog("${sensorValues.first},${sensorValues.second},${sensorValues.third}")
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ){
        Text("加速度センサー")
        Text("X: ${sensorValues.first}")
        Text("Y: ${sensorValues.second}")
        Text("Z: ${sensorValues.third}")

        Spacer(modifier = Modifier.height(12.dp))

        Switch(
            checked = isRecording,
            onCheckedChange = {
                isRecording = it

                if(it){
                    val df = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.JAPAN)
                    val date = Date(System.currentTimeMillis())
                    val fileName = df.format(date)
                    otherFileStorage = OtherFileStorage(context,fileName)
                }else{
                    otherFileStorage = null
                }
            }
        )
        Text(if(isRecording) "記録中" else "停止中")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Android_Acc_Sensing_jetpackTheme {
        AccSensingScreen()
    }
}