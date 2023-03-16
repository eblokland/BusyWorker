package land.erikblok.busyworker.Workers.ABTests

import land.erikblok.busyworker.Workers.AbstractWorker

private const val CHECK_INTERVAL = 1000000

abstract class AbstractABWorker(protected val workAmount: Int, protected val useAsRuntime: Boolean) : AbstractWorker() {

    @Volatile
    private var stop = false
    protected abstract val workload : AbstractABWorkload

    protected val numIterations: Int = if (useAsRuntime) CHECK_INTERVAL else workAmount


    override fun run(){
        if (useAsRuntime){
            val endTime = System.nanoTime() + workAmount * 1e6.toLong()
            while(workAmount <= 0 || System.nanoTime() < endTime || stop){
                workload.work()
            }
        }
        else{
            workload.work()
        }

    }

    override fun stopThread() {
        stop = true
    }
}