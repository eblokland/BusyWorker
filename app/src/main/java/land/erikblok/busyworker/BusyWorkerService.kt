package land.erikblok.busyworker

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import land.erikblok.busyworker.ThreadController.AbstractThreadController
import land.erikblok.busyworker.ThreadController.ThreadControllerFactory

const val FOREGROUND_NOT_ID = 1234
const val TAG = "BUSYWORKERSERVICE"

const val ACTION_STOP = "land.erikblok.action.STOP_WORKER"

// I may want to let this run on older devices in the future, so let's keep the sdk check for now.
@SuppressLint("ObsoleteSdkInt")
class BusyWorkerService : Service() {

    private var foregroundRunning = false
    private lateinit var nc: NotificationChannel

    private val threadControllerFactory = ThreadControllerFactory()
    private var activeThreadController: AbstractThreadController? = null

    private val runningIds: MutableSet<Int> = HashSet()

    init {
        if (Build.VERSION.SDK_INT >= 26) {
            nc = NotificationChannel("svc", "SamplerService", NotificationManager.IMPORTANCE_LOW)
        }
    }

    inner class BusyWorkerBinder : Binder() {
        fun getService(): BusyWorkerService = this@BusyWorkerService
    }

    override fun onBind(intent: Intent?): IBinder {
        return BusyWorkerBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        setForeground()
        intent?.let {
            when (it.action) {
                ACTION_STOP -> {
                    stopController()
                }
                //for now we don't have any other intents so we pass it along to the thread controller factory
                else -> {
                    val newController = threadControllerFactory.getThreadControllerFromIntent(this, intent)
                    if (newController != null){
                        startNewController(newController, startId)
                    }
                    else{
                        Log.d(TAG, "Did not successfully parse intent with action ${intent.action}")
                    }
                }
            }
        }

        if (runningIds.isEmpty()) {
            Log.i(TAG, "didn't have anything to do, stopping")
            stopSelf()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        foregroundRunning = false
        stopController()
        super.onDestroy()
    }

    /**
     * If the given ID was the last one running, stop the service now.
     * @param startId ID to check and remove from the list of running IDs
     */
    private fun checkStop(startId: Int) {
        runningIds.remove(startId)
        if (runningIds.isEmpty()) {
            stopSelf()
        }
    }

    private fun stopController(){
        Log.d(TAG, "Called stopController")
        activeThreadController?.stopThreads()
        activeThreadController = null
    }

    private fun startNewController(controller: AbstractThreadController, startId: Int){
        stopController()
        runningIds.add(startId)
        controller.startThreads{ checkStop(startId) }
        activeThreadController = controller
    }

    //endregion

    private fun setForeground() {
        if (!foregroundRunning) {
            val notman = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            if (Build.VERSION.SDK_INT >= 26) notman.createNotificationChannel(
                nc
            )
            val nb = if (Build.VERSION.SDK_INT >= 26) Notification.Builder(
                this,
                nc.id
            ) else Notification.Builder(this)
            nb.setContentText("hello i am running busy work pls do not stop me")
            nb.setSmallIcon(R.drawable.default_notification)
            val not = nb.build()
            notman.notify(FOREGROUND_NOT_ID, not)
            startForeground(FOREGROUND_NOT_ID, not)
            foregroundRunning = true
        }
    }
}