package land.erikblok.busyworker.ThreadController

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
     * @param stopCallback Callback to be called when workload is done/stopped
     */
    open fun startThreads(stopCallback: (() -> Unit)? = null) {
        cleanUpThreads()
        this.stopCallback = stopCallback
        isActive = true
    }



    /**
     * Cleans up running threads, if there are any.  Stops the threads and invokes stopCallback, if present.
     */
    protected fun cleanUpThreads() {
        threadList.forEach {
            if (it.isAlive) {
                it.stopThread()
            }
        }
        threadList.clear()
        stopCallback?.invoke()
        stopCallback = null
    }

    /**
     * Sets a stop timer to ensure that the threads don't overrun their time by too much.
     */
    protected fun setTimer(runtime: Int){
        wakeLock?.acquire((runtime + 1000).toLong())
        //handler will kill the thread if it doesn't self-exit in time
        handler.sendEmptyMessageDelayed(SUBJ_STOPTHREADS, (runtime + 1000).toLong())
    }

    /**
     * Public function to stop the currently running workloads.
     * Will release the wakelock, clean up running threads, and set isActive to false
     */
    fun stopThreads() {
        if (wakeLock?.isHeld == true) {
            wakeLock.release()
        }
        cleanUpThreads()
        isActive = false
    }
}

