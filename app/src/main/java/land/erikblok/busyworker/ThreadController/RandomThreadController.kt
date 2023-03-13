package land.erikblok.busyworker.ThreadController

import android.content.Context
import land.erikblok.busyworker.Worker.AbstractWorker
import land.erikblok.busyworker.Worker.RandomWorker


class RandomThreadController(ctx: Context) : AbstractThreadController(ctx, "busyworker:randomthreadcontroller"){

    /**
     * Starts a random workload
     * @param timestep Time in milliseconds for a chosen sub-workload to run
     * @param pauseProb Probability (from 0 to 1) of the workload sleeping on a timestep
     * @param runtime Time in seconds for the total workload to run
     * @param numClasses Number of classes to use for the workload
     * @param stopCallback Optional callback that will be executed if the workload is stopped or canceled
     */
    fun startThreads(timestep: Int, pauseProb: Float, runtime: Int, numClasses:Int,  stopCallback: (() -> Unit)? = null){
        cleanUpThreads()
        threadList.add(RandomWorker(timestep=timestep, pauseProb=pauseProb,runtime=runtime, num_classes=numClasses))
        threadList.forEach { it.start() }
        //if runtime is 0 or negative, run until stop button is pressed.
        if (runtime > 0) {
            wakeLock?.acquire((runtime + 1000).toLong())
            //handler will kill the thread if it doesn't self-exit in time
            handler.sendEmptyMessageDelayed(SUBJ_STOPTHREADS, (runtime + 1000).toLong())
        }
        startThreads(stopCallback)
    }




}