package land.erikblok.busyworker.ThreadController

import android.content.Context
import android.content.Intent
import land.erikblok.busyworker.Workers.SensorWorker.SensorWorker
import land.erikblok.busyworker.constants.*
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class SensorThreadController(ctx: Context, worker: SensorWorker, private val runtimeMillis: Long) :
    AbstractThreadController(
        ctx,
        "busyworker:SensorThreadController"
    ) {

    init {
        threadList.add(worker)
    }

    override fun startThreads(stopCallback: (() -> Unit)?){
        super.startThreads(stopCallback)
        threadList.forEach { it.start() }
        if (runtimeMillis > 0) setTimer(runtimeMillis)
    }

    companion object : ThreadControllerBuilderInterface<SensorThreadController> {
        const val ACTION_START_SENSOR = "land.erikblok.action.START_SENSOR"
        const val STC_TAG = "SENSOR_THREAD_CONTROLLER"
        override fun getControllerFromIntent(
            ctx: Context,
            intent: Intent
        ): SensorThreadController? {


            if (!(intent.hasExtra(SAMP_RATE_HZ) && intent.hasExtra(WORK_RATE_HZ) && intent.hasExtra(
                    SENSOR_TYPE
                ) && intent.hasExtra(USE_WAKEUP))
            ) {
                return null
            }

            val sensorType = intent.getIntExtra(SENSOR_TYPE, -1)
            val useWakeup = intent.getBooleanExtra(USE_WAKEUP, false)
            val workRateHz = intent.getIntExtra(WORK_RATE_HZ, -1)
            val sampleRateHz = intent.getIntExtra(SAMP_RATE_HZ, -1)

            val iterations = intent.getIntExtra(WORK_AMOUNT, 10000)

            val runtime = intent.getIntExtra(RUNTIME, -1).toLong()

            val samplePeriodUs = ((1f / sampleRateHz.toFloat()) * 1000000).roundToInt()
            val workPeriodMs = ((1f / workRateHz.toFloat()) * 1000).roundToLong()

            val worker = SensorWorker.getSensorWorkerForType(
                ctx,
                sensorType,
                useWakeup,
                samplePeriodUs,
                workPeriodMs,
                iterations
            )
                ?: return null

            return SensorThreadController(ctx, worker, runtime)
        }

    }
}