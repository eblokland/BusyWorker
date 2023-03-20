package land.erikblok.busyworker.Workers.ABTests

import land.erikblok.busyworker.Workers.AbstractWorker

private const val CHECK_INTERVAL = 1000000

abstract class AbstractABWorker(workAmount: Int, private val useAsRuntime: Boolean, private val onEnd: (() -> Unit)?, private val outerLoop: Int = 1) : AbstractWorker() {

    @Volatile
    private var stop = false
    protected abstract val workload : AbstractABWorkload

    protected val numIterations: Int = if (useAsRuntime) CHECK_INTERVAL else workAmount


    override fun run(){
        if (useAsRuntime){
            while(!stop){
                workload.work()
            }
        }
        else{
            // We need a way to get above a 32 bit int number of iterations, use the outer loop here to do it. also check here if we want to stop the loop.
            for(i in 0 until outerLoop) {
                if (stop) break
                workload.work()
            }

        }
        onEnd?.invoke()
    }

    override fun stopThread() {
        stop = true
    }
}