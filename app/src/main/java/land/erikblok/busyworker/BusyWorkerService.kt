package land.erikblok.busyworker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import land.erikblok.busyworker.ThreadController.BusyThreadController
import land.erikblok.busyworker.ThreadController.RandomThreadController
import land.erikblok.busyworker.constants.*

const val FOREGROUND_NOT_ID = 1234
const val TAG = "BUSYWORKERSERVICE"

class BusyWorkerService : Service() {

    private var foregroundRunning = false
    private lateinit var nc: NotificationChannel

    private lateinit var rtc: RandomThreadController
    private lateinit var tc: BusyThreadController

    private var busyWorkerRunning = false
    private var randomWorkerRunning = false

    private val runningIds: MutableSet<Int> = HashSet()

    init{
        if (Build.VERSION.SDK_INT >= 26) {
            nc = NotificationChannel("svc", "SamplerService", NotificationManager.IMPORTANCE_LOW)
        }
    }

    inner class BusyWorkerBinder : Binder(){
        fun getService(): BusyWorkerService = this@BusyWorkerService
    }

    override fun onBind(intent: Intent?): IBinder {
        return BusyWorkerBinder()
    }

    override fun onCreate(){
        Log.i(TAG, "called oncreate")
        rtc = RandomThreadController(this)
        tc = BusyThreadController(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int{
        setForeground()
        intent?.let{
             when (it.action){
                ACTION_STARTBUSY -> runBusyWorker(it, startId)
                ACTION_STOPBUSY -> stopBusyWorker()
                ACTION_STARTRANDOM -> runRandomWorker(it, startId)
                ACTION_STOPRANDOM -> stopRandomWorker()
                else -> false
            }
        }
        Log.i(TAG, "started")
        if (runningIds.isEmpty()) {
            Log.i(TAG, "didn't have anything to do, stopping")
            stopSelf()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy(){
        foregroundRunning = false
        rtc.stopThreads()
        tc.stopThreads()
        super.onDestroy()
    }

    /**
     * If the given ID was the last one running, stop the service now.
     * @param startId ID to check and remove from the list of running IDs
     */
    private fun checkStop(startId: Int){
        runningIds.remove(startId)
        if (runningIds.isEmpty()){
            stopSelf()
        }
    }

    private fun runBusyWorker(intent: Intent, startId: Int) {
        val runtime = intent.getIntExtra(RUNTIME, -1)
        val numThreads = intent.getIntExtra(NUM_THREADS, -1)
        val workerId = intent.getIntExtra(WORKER_ID, -1)
        if(runtime == -1 || numThreads == -1 || workerId == -1){
            Log.e(TAG, "Invalid parameters provided to busy worker")
            return
        }
        runningIds.add(startId)
        tc.startThreads(numThreads, runtime, workerId, stopCallback = {busyWorkerRunning = false; checkStop(startId)})
        busyWorkerRunning = true
    }
    private fun stopBusyWorker() {
        tc.stopThreads()
    }
    private fun stopRandomWorker() {
        rtc.stopThreads()
    }
    private fun runRandomWorker(intent: Intent, startId: Int) {
        val timestep = intent.getIntExtra(TIMESTEP, -1)
        val sleepProb = intent.getFloatExtra(SLEEP_PROB, -1.0f)
        val runtime = intent.getIntExtra(RUNTIME, -1)
        if (timestep == -1 || runtime == -1 || sleepProb == -1.0f){
            Log.e(TAG, "Invalid parameters provided to random worker, not starting.")
            return
        }
        runningIds.add(startId)
        rtc.startThreads(timestep, sleepProb, runtime * 1000, stopCallback = {randomWorkerRunning = false; checkStop(startId)})
        randomWorkerRunning = true
    }

    private fun setForeground(){
        if(!foregroundRunning){
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