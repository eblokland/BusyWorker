package land.erikblok.busyworker.ThreadController

import android.content.Context
import land.erikblok.busyworker.Worker.AbstractWorker
import land.erikblok.busyworker.Worker.RandomWorker


class RandomThreadController(ctx: Context) : AbstractThreadController(ctx){

    fun startThreads(timestep: Int, pauseProb: Float, runtime: Int, stopCallback: (() -> Unit)? = null){
        cleanUpThreads()
        threadList.add(RandomWorker(timestep=timestep, pauseProb=pauseProb,runtime=runtime))

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