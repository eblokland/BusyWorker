package land.erikblok.busyworker.ThreadController

import android.content.Context
import android.content.Intent
import android.util.Log
import land.erikblok.busyworker.TAG
import land.erikblok.busyworker.Workers.AbstractWorker
import land.erikblok.busyworker.Workers.BusyWorker.AlsoAVeryHardWorker
import land.erikblok.busyworker.Workers.BusyWorker.VeryHardWorker
import land.erikblok.busyworker.constants.NUM_THREADS
import land.erikblok.busyworker.constants.RUNTIME
import land.erikblok.busyworker.constants.WORKER_ID

/**
 * Busy thread controller class, that will control execution of one or more runs of busywork threads.
 * @param ctx Context necessary to set up a handler.
 */
class BusyThreadController(ctx: Context, val numThreads: Int, val runtime: Int, val workerId: Int = 0,) :
    AbstractThreadController(ctx, "busyworker:busythreadcontroller") {

    companion object : ThreadControllerBuilderInterface<BusyThreadController>{
        const val ACTION_STARTBUSY = "land.erikblok.action.START_BUSY"
        const val ACTION_STOPBUSY = "land.erikblok.action.STOP_BUSY"
        override fun getControllerFromIntent(ctx: Context, intent: Intent) : BusyThreadController? {
            val runtime = intent.getIntExtra(RUNTIME, -1)
            val numThreads = intent.getIntExtra(NUM_THREADS, -1)
            val workerId = intent.getIntExtra(WORKER_ID, -1)
            if (runtime == -1 || numThreads == -1 || workerId == -1) {
                Log.e(TAG, "Invalid parameters provided to busy worker")
                return null
            }
            return BusyThreadController(ctx, numThreads, runtime, workerId)
        }
    }
    /**
     * Starts one or more busy work threads, that will run some math ops.
     * @param numThreads Number of threads to start
     * @param runtime Length of time in milliseconds to run
     * @param workerId Selects one of (currently) two workers, with id 0 and 1 respectively.
     * @param stopCallback Optional callback to be called when threads finish or are killed.
     */
    override fun startThreads(stopCallback: (() -> Unit)?) {
        cleanUpThreads()
        for (i in 1..numThreads) {
            threadList.add(getWorker(workerId))
        }
        threadList.forEach { it.start() }
        //if runtime is 0 or negative, run until stop button is pressed.
        if (runtime > 0) {
            wakeLock?.acquire((runtime + 1000).toLong())
            handler.sendEmptyMessageDelayed(SUBJ_STOPTHREADS, runtime.toLong())
        }
        super.startThreads(stopCallback)
    }


}


/**
 * Static function that assigns worker classes to worker IDs.
 * Workers must be in separate classes, else everything will get inlined and we will not know
 * which one was running from the stack trace.
 */
fun getWorker(id: Int): AbstractWorker {
    return when (id) {
        0 -> VeryHardWorker()
        1 -> AlsoAVeryHardWorker()
        else -> VeryHardWorker()
    }
}