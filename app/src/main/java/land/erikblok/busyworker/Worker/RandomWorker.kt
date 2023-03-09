package land.erikblok.busyworker.Worker

import android.util.Log
import land.erikblok.busyworker.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

val RAND_TAG = "RANDOM_WORKER"

/**
 * @param timestep millis between workload selection
 * @param pauseProb probability from 0 to 1 determining how likely the worker is to pause for a
 *  timestep.
 *  @param runtime total runtime of the randomworker including pauses, in millis.
 */
class RandomWorker(val timestep: Long, val runtime: Long, val pauseProb: Double) :
    AbstractWorker() {
    @Volatile
    var stop: Boolean = false
    val workloads: MutableList<AbstractWorkload> = ArrayList()
    val sleepLoad = SleepWorkload(timestep)
    val randomGenerator = Random()
    val runtimeTotal: MutableMap<AbstractWorkload, Long> = HashMap()

    init {
        workloads.addAll(arrayOf(Workload1(timestep), Workload2(timestep), Workload3(timestep)))
        for (load in workloads) {
            runtimeTotal[load] = 0
        }
        runtimeTotal[sleepLoad] = 0
    }

    override fun stopThread() {
        if(!isAlive) return
        stop = true
        Log.i("early stopped!", RAND_TAG)
    }

    override fun run() {
        val finalEndTime = System.nanoTime() + runtime * 1e6
        var loopStartTime: Long = 0
        while (System.nanoTime().also { loopStartTime = it } < finalEndTime && !stop) {
            val worker = pickWorker()
            worker.work()
            val elapsedNanos = System.nanoTime() - loopStartTime
            runtimeTotal[worker] = (runtimeTotal[worker] ?: 0) + elapsedNanos
        }
        printLogs()
    }

    fun pickWorker(): AbstractWorkload {
        val pause = randomGenerator.nextDouble() < pauseProb
        if (pause) {
            return sleepLoad
        }
        val id = randomGenerator.nextInt(workloads.size)
        return workloads[id]
    }

    fun printLogs() {
        for (wl in workloads) {
            printLog(wl, runtimeTotal[wl] ?: 0)
        }
        printLog(sleepLoad, runtimeTotal[sleepLoad] ?: 0)
    }

    fun printLog(aw: AbstractWorkload, runtime: Long) {
        Log.i("${aw::class.simpleName} ran for $runtime nanoseconds", RAND_TAG)
    }
}