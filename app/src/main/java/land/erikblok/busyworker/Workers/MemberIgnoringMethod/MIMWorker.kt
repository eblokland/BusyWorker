package land.erikblok.busyworker.Workers.MemberIgnoringMethod

import land.erikblok.busyworker.Workers.AbstractWorker

const val CHECK_INTERVAL = 1000000


class MIMWorker(useFixed: Boolean, private val iterations: Int, private val useAsRuntime: Boolean) : AbstractWorker() {

    private val workload : AbstractMemberIgnoringMethodWorkload

    @Volatile
    private var stop = false

    init {
        val numIterations = if (useAsRuntime) CHECK_INTERVAL else iterations
        workload =
            if (useFixed) FixedMemberIgnoringMethodWorkload(numIterations)
            else MemberIgnoringMethodWorkload(numIterations)
    }

    override fun run(){
        if (useAsRuntime){
            val endTime = System.nanoTime() + iterations * 1e6.toLong()
            while(iterations <= 0 || System.nanoTime() < endTime || stop){
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