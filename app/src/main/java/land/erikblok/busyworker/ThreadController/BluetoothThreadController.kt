package land.erikblok.busyworker.ThreadController

import android.content.Context
import android.content.Intent
import android.util.Log
import land.erikblok.busyworker.Workers.Bluetooth.BluetoothWorker
import land.erikblok.busyworker.Workers.RandomWorker.RandomWorker
import land.erikblok.busyworker.constants.*

/**
 * Class to control a Bluetooth worker.  In order to test the bluetooth energy consumption more effectively, it will
 * also contain the option to start a number of busy threads with a percentage of time to sleep.
 * This will allow my tool to measure the energy consumption of the system constantly, since there needs
 * to be an active thread in order to measure
 * @param ctx Context
 * @param numBusyThreads Number of busy threads to run
 * @param sleepChance probability that a busy thread will sleep for some timestep.
 * @param timeStep Timestep for the busy threads to randomly select a state, set to 100 ms by default.
 * @param runtimeMillis Optional amount of time for the random worker
 */
class BluetoothThreadController private constructor(
    ctx: Context,
    numBusyThreads: Int,
    sleepChance: Float = 0f,
    timeStep: Int = 100,
    private val runtimeMillis: Long = 0,
    btWorker: BluetoothWorker,
) :
    AbstractThreadController(ctx, "busyworker:BluetoothThreadController") {


    init {
        for (i in 0 until numBusyThreads) {
            threadList.add(RandomWorker(timeStep, runtimeMillis, sleepChance, 1))
        }
        threadList.add(btWorker)
    }

    override fun startThreads(stopCallback: (() -> Unit)?) {
        super.startThreads(stopCallback)
        threadList.forEach { it.start() }
        if (runtimeMillis > 0) setTimer(runtimeMillis)
    }

    companion object : ThreadControllerBuilderInterface<BluetoothThreadController> {
        const val ACTION_START_BLUETOOTH = "land.erikblok.action.START_BLUETOOTH"
        const val BTC_TAG = "BLUETOOTH_THREAD_CONTROLLER"

        override fun getControllerFromIntent(
            ctx: Context,
            intent: Intent
        ): BluetoothThreadController? {

            if (!(intent.hasExtra(SCAN_ACTIVE_MILLIS) && intent.hasExtra(SCAN_PERIOD_MILLIS))) {
                Log.d(BTC_TAG, "Missing "+ if (intent.hasExtra(SCAN_ACTIVE_MILLIS)) SCAN_ACTIVE_MILLIS else "" +
                        if (intent.hasExtra(SCAN_PERIOD_MILLIS)) SCAN_PERIOD_MILLIS else ""
                )
                return null
            }

            val numBusyThreads = intent.getIntExtra(NUM_THREADS, 0)
            val sleepChance = intent.getFloatExtra(SLEEP_PROB, 0f)
            val timeStep = intent.getIntExtra(TIMESTEP, 100)
            val runtimeMillis = intent.getIntExtra(RUNTIME, 0).toLong() * 1000

            val scanActiveMillis = intent.getIntExtra(SCAN_ACTIVE_MILLIS, -1).toLong()
            val scanPeriodMillis = intent.getIntExtra(SCAN_PERIOD_MILLIS, -1).toLong()


            return getController(ctx, numBusyThreads, sleepChance, timeStep, runtimeMillis, scanActiveMillis, scanPeriodMillis)
        }

        fun getController(
            ctx: Context,
            numBusyThreads: Int,
            sleepChance: Float = 0f,
            timeStep: Int = 100,
            runtimeMillis: Long = 0,
            scanActiveMillis: Long,
            scanPeriodMillis: Long,
        ): BluetoothThreadController? {
            val btWorker = BluetoothWorker.constructBluetoothWorker(
                ctx,
                scanPeriodMillis,
                scanActiveMillis) ?: return null
            return BluetoothThreadController(ctx, numBusyThreads, sleepChance, timeStep, runtimeMillis, btWorker)
        }

    }
}