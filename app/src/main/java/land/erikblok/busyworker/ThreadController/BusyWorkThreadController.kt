package land.erikblok.busyworker.ThreadController

import android.content.Context


class BusyThreadController(ctx: Context) : AbstractThreadController(ctx) {
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