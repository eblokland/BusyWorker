package land.erikblok.busyworker.ThreadController

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Message
import android.os.PowerManager
import land.erikblok.busyworker.Workers.AbstractWorker

/**
 * Abstract class for thread controllers, handles setting up wakelock and stop timer.
 * @param ctx Context used for setting up a handler.
 * @param WAKE_LOCK_TAG Tag used to acquire wakelocks, to be provided by a child class.
 */
abstract class AbstractThreadController(ctx: Context, WAKE_LOCK_TAG: String) {
    val SUBJ_STOPTHREADS = 238593
    val threadList: MutableList<AbstractWorker> = ArrayList()
    val wakeLock: PowerManager.WakeLock?
    val handler: Handler
    var stopCallback: (() -> Unit)? = null
    @Volatile
    var isActive = false

    init {
        handler = Handler(ctx.mainLooper, object : Handler.Callback {
            override fun handleMessage(msg: Message): Boolean {
                if (msg.what == SUBJ_STOPTHREADS) {
                    stopThreads()
                    return true
                }
                return false
            }
        })
        val powerMan = ctx.getSystemService(Context.POWER_SERVICE)
        wakeLock = if (powerMan is PowerManager) {
            powerMan.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK_TAG)
        } else {
            null
        }
    }

    /**
     * Base function to be called after threads are started, performs some bookkeeping
     * It is now considered unsupported to call startThreads while isActive is true,
     * so startThreads will immediately return.
     * @param stopCallback Callback to be called when workload is done/stopped
     */
    @SuppressLint("WakelockTimeout") //I am intentionally abusing wakelocks here to ensure that the test does not get suspended.
    @Synchronized
    open fun startThreads(stopCallback: (() -> Unit)? = null) {
        if (isActive) return
        this.stopCallback = stopCallback
        isActive = true
        wakeLock?.acquire()
    }


    /**
     * Sets a stop timer for time-based threads.
     * Also acquires a wakelock that runs for 1 second beyond the timer.
     * @param runtime Amount of time that the threads should run for.
     */
    protected fun setTimer(runtime: Long) {
        //if (wakeLock != null && !wakeLock.isHeld) wakeLock.acquire((runtime + 1000))
        handler.sendEmptyMessageDelayed(SUBJ_STOPTHREADS, (runtime))
    }

    /**
     * Public function to stop the currently running workloads.
     * Will release the wakelock, clean up running threads, and set isActive to false
     * As I found out the hard way, the Android system may be faster at killing the service than the
     * service is at shutting itself down!  This function has been made synchronized
     * to prevent this from happening, as stopThreads was getting pre-empted by itself :(
     */
    @Synchronized
    fun stopThreads() {
        if (!isActive) return
        if (wakeLock?.isHeld == true) {
            wakeLock.release()
        }
        cleanUpThreads()
        isActive = false
    }

    /**
     * Cleans up running threads, if there are any.  Stops the threads and invokes stopCallback, if present.
     */
    private fun cleanUpThreads() {
        threadList.forEach {
            if (it.isAlive) {
                it.stopThread()
            }
        }
        threadList.clear()
        stopCallback?.invoke()
        stopCallback = null
        handler.removeMessages(SUBJ_STOPTHREADS)
    }
}

