package land.erikblok.busyworker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import land.erikblok.busyworker.ThreadController.RandomThreadController
import land.erikblok.busyworker.ThreadController.BusyThreadController
import land.erikblok.busyworker.constants.*
import land.erikblok.busyworker.ui.theme.BusyWorkerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            BusyWorkerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(modifier = Modifier.verticalScroll(state = rememberScrollState())) {
                        BusyThreadStartComponents(this@MainActivity)
                        RandomThreadStartComponents(this@MainActivity)
                    }
                }
            }
        }
    }
}

//Use onValueChange to receive the new integer, if there is one.
@Composable
fun IntegerTextField(
    isValid: Boolean,
    onValueChange: ((Int?) -> Unit),
    placeholder: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
) {

    var textValue by remember { mutableStateOf("") }

    return OutlinedTextField(
        value = textValue,
        onValueChange = { str: String ->
            onValueChange(str.toIntOrNull())
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
fun FloatTextField(
    isValid: Boolean,
    onValueChange: ((Float?) -> Unit),
    placeholder: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
) {
    var textValue by remember { mutableStateOf("") }

    return OutlinedTextField(
        value = textValue,
        onValueChange = { str: String ->
            onValueChange(str.toFloatOrNull())
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
fun BusyThreadStartComponents(ctx: Context? = null) {
    fun startThreads(duration: Int, numThreads: Int, workerId: Int) {
        if (ctx != null) {
            Intent(ctx, BusyWorkerService::class.java).apply {
                this.action = ACTION_STARTBUSY
                this.putExtra(RUNTIME, duration)
                this.putExtra(NUM_THREADS, numThreads)
                this.putExtra(WORKER_ID, workerId)
            }.also {
                Log.i(TAG, "starting busy thread")
                ctx.startService(it)
            }
        }

    }
    Column {
        var numThreads by remember { mutableStateOf<Int>(1) }
        var currentRuntime by remember { mutableStateOf<Int>(0) }
        var threadsValid by remember { mutableStateOf(false) }
        var runtimeValid by remember { mutableStateOf(true) }
        val startWorkerWithId: (Int) -> Unit =
            { workerId: Int -> startThreads(currentRuntime * 1000, numThreads, workerId) }

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
        }
        Row {
            Button(
                onClick = {
                    if (ctx != null) {
                        Intent(ctx, BusyWorkerService::class.java).apply {
                            this.action = ACTION_STOPBUSY
                        }.also {
                            ctx.startService(it)
                        }
                    }
                },
                enabled = true,
                content = { Text("Stop threads if running") },
            )
        }
    }


}

@Composable
fun RandomThreadStartComponents(ctx: Context? = null) {
    var runtime by remember { mutableStateOf(1) }
    var pauseProb by remember { mutableStateOf(0.5f) }
    var runtimeValid by remember { mutableStateOf(false) }
    var pauseProbValid by remember { mutableStateOf(false) }
    var timestep by remember { mutableStateOf(100) }
    var timestepValid by remember { mutableStateOf(false) }
    var numClasses by remember { mutableStateOf(3) }
    var numClassesValid by remember { mutableStateOf(false) }

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
    FloatTextField(isValid = pauseProbValid, onValueChange = { prob ->
        pauseProbValid = (prob != null) && prob >= 0.0 && prob <= 1.0
        //use !! here because we just determined prob was non-null, kotlin
        //isn't able to figure that out though.
        pauseProb = if (pauseProbValid) prob!! else pauseProb
    },
        placeholder = { Text("pause probability") },
        label = { Text("Probability that the thread will sleep instead of selecting a workload") })
    Spacer(modifier = Modifier.height(5.dp))
    IntegerTextField(
        isValid = numClassesValid,
        onValueChange = { classes ->
            numClassesValid = classes != null && classes >= 1
            numClasses = if (numClassesValid) classes!! else numClasses
        },
        placeholder = { Text("num classes") },
        label = { Text("Number of classes to split load between") },
    )
    Spacer(modifier = Modifier.height(5.dp))
    Button(
        onClick = {
            if (ctx != null) {
                Intent(ctx, BusyWorkerService::class.java).apply {
                    this.action = ACTION_STARTRANDOM
                    this.putExtra(TIMESTEP, timestep)
                    this.putExtra(RUNTIME, runtime)
                    this.putExtra(SLEEP_PROB, pauseProb)
                    this.putExtra(NUM_CLASSES, numClasses)
                }.also {
                    ctx.startService(it)
                }
            }
        },
        enabled = timestepValid && pauseProbValid && runtimeValid,
        content = { Text("Start random workload") }
    )
    Spacer(modifier = Modifier.height(5.dp))
    Button(
        onClick = {
            if (ctx != null) {
                Intent(ctx, BusyWorkerService::class.java).apply {
                    this.action = ACTION_STOPRANDOM
                }.also {
                    ctx.startService(it)
                }
            }
        },
        enabled = true,
        content = { Text("Stop random threads if running") }
    )


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