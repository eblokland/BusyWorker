package land.erikblok.busyworker.Workers.RandomWorker

import android.util.Log
import land.erikblok.busyworker.Workers.AbstractWorker
import land.erikblok.busyworker.Workers.AbstractWorkload
import java.util.*

const val RAND_TAG = "RANDOM_WORKER"
private const val MAGIC_REFERENCE_NUM = 5
/**
 * @param timestep millis between workload selection
 * @param pauseProb probability from 0 to 1 determining how likely the worker is to pause for a
 *  timestep.
 *  @param runtimeMillis total runtime of the randomworker including pauses, in millis.  A value of
 *  <=0 means that the thread will run until stopped.
 */
class RandomWorker(
    private val timestep: Int,
    private val runtimeMillis: Long,
    private val pauseProb: Float,
    private var num_classes: Int
) :
    AbstractWorker() {
    @Volatile
    private var stop: Boolean = false
    private val workloads: List<AbstractWorkload>
    private val sleepLoad = SleepWorkload(timestep)
    private val randomGenerator = Random()
    private val runtimeTotal: MutableMap<AbstractWorkload, Long> = HashMap()

    init {
        val baseWorkloads =
            arrayOf(
                Workload1(timestep),
                Workload2(timestep),
                Workload3(timestep),
                Workload4(timestep),
                Workload5(timestep),
                Workload6(timestep)
            )

        for (load in baseWorkloads) {
            runtimeTotal[load] = 0
        }

        runtimeTotal[sleepLoad] = 0
        num_classes = baseWorkloads.size.coerceAtMost(num_classes)

        // We want a non-uniform random distribution, otherwise there's probably nothing to look at!.
        // To do that, we'll copy the reference to each abstract workload some number of times.
        // The number of extra references will be a random number from 0 to some magic number for now.
        //also, go ahead and remove the extras.
        workloads = ArrayList()

        //ensure that the head of the array is each class in order.
        for(i in 0 until num_classes){
            workloads.add(baseWorkloads[i])
        }

        for (i in 0 until num_classes){
            val numInserts = randomGenerator.nextInt(MAGIC_REFERENCE_NUM)
            for (j in 0..numInserts){
                workloads.add(baseWorkloads[i])
            }
        }

    }

    override fun stopThread() {
        if (!isAlive) return
        stop = true
        Log.i("early stopped!", RAND_TAG)
    }

    override fun run() {
        val useTimer = runtimeMillis > 0
        val finalEndTime = System.nanoTime() + runtimeMillis * 1000000
        var loopStartTime: Long = 0

        while (!stop) {
            loopStartTime = System.nanoTime()
            if(useTimer && loopStartTime > finalEndTime) break;
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
        val id = randomGenerator.nextInt(workloads.size)
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