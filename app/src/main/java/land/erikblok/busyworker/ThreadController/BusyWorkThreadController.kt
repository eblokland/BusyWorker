package land.erikblok.busyworker.ThreadController

import android.content.Context
import land.erikblok.busyworker.Worker.AbstractWorker
import land.erikblok.busyworker.Worker.AlsoAVeryHardWorker
import land.erikblok.busyworker.Worker.VeryHardWorker


/**
 * Busy thread controller class, that will control execution of one or more runs of busywork threads.
 * @param ctx Context necessary to set up a handler.
 */
class BusyThreadController(ctx: Context) : AbstractThreadController(ctx, "busyworker:busythreadcontroller") {

    /**
     * Starts one or more busy work threads, that will run some math ops.
     * @param numThreads Number of threads to start
     * @param runtime Length of time in milliseconds to run
     * @param workerId Selects one of (currently) two workers, with id 0 and 1 respectively.
     * @param stopCallback Optional callback to be called when threads finish or are killed.
     */
    fun startThreads(numThreads: Int, runtime: Int, workerId: Int = 0, stopCallback: (() -> Unit)? = null) {
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
        startThreads(stopCallback)
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