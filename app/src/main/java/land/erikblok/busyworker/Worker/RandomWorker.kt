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
class RandomWorker(
    val timestep: Int,
    val runtime: Int,
    val pauseProb: Float,
    private var num_classes: Int
) :
    AbstractWorker() {
    @Volatile
    private var stop: Boolean = false
    private val workloads: MutableList<AbstractWorkload> = ArrayList()
    private val sleepLoad = SleepWorkload(timestep)
    private val randomGenerator = Random()
    private val runtimeTotal: MutableMap<AbstractWorkload, Long> = HashMap()

    init {
        workloads.addAll(
            arrayOf(
                Workload1(timestep),
                Workload2(timestep),
                Workload3(timestep),
                Workload4(timestep),
                Workload5(timestep),
                Workload6(timestep)
            )
        )
        for (load in workloads) {
            runtimeTotal[load] = 0
        }
        runtimeTotal[sleepLoad] = 0
        num_classes = workloads.size.coerceAtMost(num_classes)
    }

    override fun stopThread() {
        if (!isAlive) return
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

    /**
     * Picks a workload from the list of available workloads, or sleeps.
     */
    private fun pickWorker(): AbstractWorkload {
        // first decide whether or not to sleep, with probability pauseProb
        val pause = randomGenerator.nextDouble() < pauseProb
        if (pause) {
            return sleepLoad
        }
        // If we don't sleep, pick a random workload.
        val id = randomGenerator.nextInt(num_classes)
        return workloads[id]
    }

    private fun printLogs() {
        fun printLog(aw: AbstractWorkload, runtime: Long) {
            Log.i(RAND_TAG, "${aw::class.simpleName}, $runtime")
        }

        Log.i(RAND_TAG, "NUM CLASSES: $num_classes")
        Log.i(RAND_TAG, "Classname, runtime (ns)")
        for (i in 0 until num_classes) {
            val wl = workloads[i]
            printLog(wl, runtimeTotal[wl] ?: 0)
        }
        printLog(sleepLoad, runtimeTotal[sleepLoad] ?: 0)
    }
}