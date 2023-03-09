package land.erikblok.busyworker.ThreadController

import android.content.Context
import land.erikblok.busyworker.Worker.AbstractWorker
import land.erikblok.busyworker.Worker.RandomWorker


class RandomThreadController(ctx: Context) : AbstractThreadController(ctx){

    fun workerToString(worker: AbstractWorker?): String{
        return if (worker == null) "Pause" else (worker::class.simpleName ?: "unknown")
    }


    fun startThreads(timestep: Long, pauseProb: Double, runtime: Long){
        cleanUpThreads()
        threadList.add(RandomWorker(timestep=timestep, pauseProb=pauseProb,runtime=runtime))

        threadList.forEach { it.start() }
        //if runtime is 0 or negative, run until stop button is pressed.
        if (runtime > 0) {
            wakeLock?.acquire(runtime + 1000)
            //handler will kill the thread if it doesn't self-exit in time
            handler.sendEmptyMessageDelayed(SUBJ_STOPTHREADS, runtime + 1000)
        }
    }




}