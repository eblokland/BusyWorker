package land.erikblok.busyworker.Workers.ABTests

import land.erikblok.busyworker.Workers.AbstractWorker

private const val CHECK_INTERVAL = 1000000

abstract class AbstractABWorker(workAmount: Int, private val useAsRuntime: Boolean, private val onEnd: (() -> Unit)?) : AbstractWorker() {

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
            workload.work()
        }
        onEnd?.invoke()
    }

    override fun stopThread() {
        stop = true
    }
}