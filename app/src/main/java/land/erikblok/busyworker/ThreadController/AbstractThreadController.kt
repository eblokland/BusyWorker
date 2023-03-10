package land.erikblok.busyworker.ThreadController

import android.content.Context
import android.os.Handler
import android.os.Message
import android.os.PowerManager
import land.erikblok.busyworker.Worker.AbstractWorker
import land.erikblok.busyworker.Worker.AlsoAVeryHardWorker
import land.erikblok.busyworker.Worker.VeryHardWorker
import java.util.*
import kotlin.collections.ArrayList

fun getWorker(id: Int): AbstractWorker {
    return when (id) {
        0 -> VeryHardWorker()
        1 -> AlsoAVeryHardWorker()
        else -> VeryHardWorker()
    }
}

/**
 * Abstract class for thread controllers, handles setting up wakelock and
 */
abstract class AbstractThreadController(ctx: Context) {
    val WAKE_LOCK_TAG = "busyworker:busywakelock"
    val SUBJ_STOPTHREADS = 238593

    val threadList: MutableList<AbstractWorker> = ArrayList()
    val timer: Timer = Timer()
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
        if (powerMan is PowerManager) {
            wakeLock = powerMan.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK_TAG)
        } else {
            wakeLock = null
        }
    }

    protected fun startThreads(stopCallback: (() -> Unit)?) {
        this.stopCallback = stopCallback
        isActive = true
    }

    fun cleanUpThreads() {
        threadList.forEach {
            if (it.isAlive) {
                it.stopThread()
            }
        }
        threadList.clear()
    }

    fun stopThreads() {
        if (wakeLock?.isHeld == true) {
            wakeLock.release()
        }
        cleanUpThreads()
        stopCallback?.invoke()
        stopCallback = null
        isActive = false
    }
}