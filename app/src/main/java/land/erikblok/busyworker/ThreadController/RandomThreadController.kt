package land.erikblok.busyworker.ThreadController

import android.content.Context
import android.content.Intent
import android.util.Log
import land.erikblok.busyworker.TAG
import land.erikblok.busyworker.Workers.RandomWorker.RandomWorker
import land.erikblok.busyworker.constants.NUM_CLASSES
import land.erikblok.busyworker.constants.RUNTIME
import land.erikblok.busyworker.constants.SLEEP_PROB
import land.erikblok.busyworker.constants.TIMESTEP


class RandomThreadController(
    ctx: Context,
    private val timestep: Int,
    private val pauseProb: Float,
    private val runtimeMillis: Int,
    private val numClasses: Int
) : AbstractThreadController(ctx, "busyworker:randomthreadcontroller") {

    companion object : ThreadControllerBuilderInterface<RandomThreadController> {
        const val ACTION_STARTRANDOM = "land.erikblok.action.START_RANDOM"
        const val ACTION_STOPRANDOM = "land.erikblok.action.STOP_RANDOM"

        override fun getControllerFromIntent(
            ctx: Context,
            intent: Intent
        ): RandomThreadController? {
            val timestep = intent.getIntExtra(TIMESTEP, -1)
            val pauseProb = intent.getFloatExtra(SLEEP_PROB, -1.0f)
            val runtimeSeconds = intent.getIntExtra(RUNTIME, -1)
            val numClasses = intent.getIntExtra(NUM_CLASSES, -1)
            if (timestep == -1 || runtimeSeconds == -1 || pauseProb == -1.0f || numClasses == -1) {
                Log.e(TAG, "Invalid parameters provided to random worker, not starting.")
                return null
            }
            return RandomThreadController(ctx, timestep, pauseProb, runtimeSeconds * 1000, numClasses)
        }
    }

    /**
     * Starts a random workload
     * @param timestep Time in milliseconds for a chosen sub-workload to run
     * @param pauseProb Probability (from 0 to 1) of the workload sleeping on a timestep
     * @param runtime Time in seconds for the total workload to run
     * @param numClasses Number of classes to use for the workload
     * @param stopCallback Optional callback that will be executed if the workload is stopped or canceled
     */
    override fun startThreads(stopCallback: (() -> Unit)?) {
        super.startThreads(stopCallback)
        threadList.add(
            RandomWorker(
                timestep = timestep,
                pauseProb = pauseProb,
                runtimeMillis = runtimeMillis,
                num_classes = numClasses
            )
        )
        threadList.forEach { it.start() }
        //if runtime is 0 or negative, run until stop button is pressed.
        if (runtimeMillis > 0) {
            setTimer(runtimeMillis)
        }
    }


}