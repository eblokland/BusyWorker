package land.erikblok.busyworker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import land.erikblok.busyworker.ThreadController.RandomThreadController
import land.erikblok.busyworker.ThreadController.BusyThreadController
import land.erikblok.busyworker.ui.theme.BusyWorkerTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    lateinit var tc: BusyThreadController
    lateinit var rtc: RandomThreadController
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        tc = BusyThreadController(this)
        rtc = RandomThreadController(this)
        setContent {
            BusyWorkerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column() {
                        BusyThreadStartComponents(tc)
                        RandomThreadStartComponents(rtc)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tc.stopThreads()
    }


}

//Use onValueChange to receive the new integer, if there is one.
@Composable
fun IntegerTextField(
    isValid: Boolean,
    onValueChange: ((Long?) -> Unit),
    placeholder: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
) {

    var textValue by remember { mutableStateOf("") }

    return OutlinedTextField(
        value = textValue,
        onValueChange = { str: String ->
            onValueChange(str.toLongOrNull())
            textValue = str
        },
        placeholder = placeholder,
        label = label,
        isError = !isValid,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            autoCorrect = false,
        )
    )
}

@Composable
fun DoubleTextField(
    isValid: Boolean,
    onValueChange: ((Double?) -> Unit),
    placeholder: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
) {
    var textValue by remember { mutableStateOf("") }

    return OutlinedTextField(
        value = textValue,
        onValueChange = { str: String ->
            onValueChange(str.toDoubleOrNull())
            textValue = str
        },
        placeholder = placeholder,
        label = label,
        isError = !isValid,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            autoCorrect = false,
        )
    )
}


@Composable
fun BusyThreadStartComponents(tc: BusyThreadController? = null) {
    fun startThreads(duration: Long, numThreads: Int, workerId: Int) {
        tc?.startThreads(numThreads, duration, workerId)
    }
    Column {
        var numThreads by remember { mutableStateOf<Long>(1) }
        var currentRuntime by remember { mutableStateOf<Long>(0) }
        var threadsValid by remember { mutableStateOf(false) }
        var runtimeValid by remember { mutableStateOf(true) }
        val startWorkerWithId: (Int) -> Unit =
            { workerId: Int -> startThreads(currentRuntime * 1000, numThreads.toInt(), workerId) }

        IntegerTextField(
            isValid = threadsValid,
            onValueChange = { threads ->
                threadsValid = threads != null
                numThreads = threads ?: numThreads
            },
            placeholder = { Text("thread count") },
            label = { Text("Number of busy threads to run") },
        )
        Spacer(modifier = Modifier.height(5.dp))
        IntegerTextField(
            isValid = runtimeValid,
            onValueChange = { time ->
                runtimeValid = time != null
                currentRuntime = time ?: currentRuntime
            },
            placeholder = { Text("runtime") },
            label = { Text("Amount of time to do busywork") },
        )
        Spacer(modifier = Modifier.height(5.dp))
        Row {
            Button(
                onClick = { startWorkerWithId(0) },
                enabled = threadsValid && runtimeValid,
                content = { Text("Start threads 0") },
            )
            Spacer(modifier = Modifier.width(5.dp))
            Button(
                onClick = { startWorkerWithId(1) },
                enabled = threadsValid && runtimeValid,
                content = { Text("Start threads 1") },
            )
            Spacer(modifier = Modifier.width(5.dp))
            Button(
                onClick = { tc?.stopThreads() },
                enabled = true,
                content = { Text("Stop threads if running") },
            )
        }

    }
}

@Composable
fun RandomThreadStartComponents(rtc: RandomThreadController? = null) {
    var runtime by remember { mutableStateOf<Long>(1) }
    var pauseProb by remember { mutableStateOf<Double>(0.5) }
    var runtimeValid by remember { mutableStateOf(false) }
    var pauseProbValid by remember { mutableStateOf(false) }
    var timestep by remember { mutableStateOf<Long>(100) }
    var timestepValid by remember { mutableStateOf(false) }

    IntegerTextField(isValid = runtimeValid, onValueChange = { time ->
        runtimeValid = time != null
        runtime = time ?: runtime
    },
        placeholder = { Text("runtime") },
        label = { Text("Total runtime") })
    Spacer(modifier = Modifier.height(5.dp))
    IntegerTextField(
        isValid = timestepValid,
        onValueChange = { ts ->
            timestepValid = ts != null
            timestep = ts ?: timestep
        },
        placeholder = { Text("timestep") },
        label = { Text("Amount of time per random selection") }
    )
    Spacer(modifier = Modifier.height(5.dp))
    DoubleTextField(isValid = pauseProbValid, onValueChange = { prob ->
        pauseProbValid = (prob != null) && prob >= 0.0 && prob <= 1.0
        //use !! here because we just determined prob was non-null, kotlin
        //isn't able to figure that out though.
        pauseProb = if (pauseProbValid) prob!! else pauseProb
    },
        placeholder = { Text("pause probability") },
        label = { Text("Probability that the thread will sleep instead of selecting a workload") })
    Spacer(modifier = Modifier.height(5.dp))
    Row() {
        Button(
            onClick = {
                rtc?.startThreads(
                    timestep = timestep,
                    pauseProb = pauseProb,
                    runtime = runtime
                )
            },
            enabled = timestepValid && pauseProbValid && runtimeValid,
            content = { Text("Start random workload") }
        )
        Spacer(modifier = Modifier.height(5.dp))
        Button(
            onClick = { rtc?.stopThreads() },
            enabled = true,
            content = { Text("Stop random threads if running") }
        )
    }

}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun RandomPreview() {
    BusyWorkerTheme {
        RandomThreadStartComponents()
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BusyWorkerTheme {
        BusyThreadStartComponents()
    }
}