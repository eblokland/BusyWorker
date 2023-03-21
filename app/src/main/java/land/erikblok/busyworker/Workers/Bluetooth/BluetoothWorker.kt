package land.erikblok.busyworker.Workers.Bluetooth

import android.Manifest
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Context.BLUETOOTH_SERVICE
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import land.erikblok.busyworker.Workers.AbstractWorker

/**
 * Worker class that will perform Bluetooth LE scans.  It will scan using a fixed period, and a fixed amount of time
 * scanning per period.
 * @param ctx Context to get bluetooth manager
 * @param scanActiveMillis Amount of time to spend actively scanning per scan period
 * @param scanPeriodMillis Length of a scan period (active + non-active)
 * @param timestepMillis optional parameter to control the amount of time between checks to exit thread.
 * Set to 1 sec by default.
 */
class BluetoothWorker(
    private val leScanner: BluetoothLeScanner,
    private val scanPeriodMillis: Long,
    private val scanActiveMillis: Long,
    private val timestepMillis: Long = 1000
) : AbstractWorker() {

    @Volatile
    private var stop: Boolean = false

    //It's really feeling like 2008 in here
    private val leScanCallback: ScanCallback
    private val scanInactiveMillis: Long = scanPeriodMillis - scanActiveMillis


    init {
        //this is just a dummy object for now, maybe do something with it later.
        leScanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
                Log.d(BW_TAG, "onScanResult called for $callbackType")
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                super.onBatchScanResults(results)
                Log.d(BW_TAG, "onBatchScanResults called")

            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                Log.d(BW_TAG, "onScanFailed called for $errorCode")
            }
        }
    }

    override fun run() {
        try {
            while (!stop) {
                leScanner.startScan(leScanCallback)
                Log.d(BW_TAG, "Starting scan")
                sleep(scanActiveMillis)
                leScanner.stopScan(leScanCallback)
                Log.d(BW_TAG, "stopping scan")
                if(stop) break
                val endTime = System.nanoTime() + (scanInactiveMillis * 1000000)
                var remainingTime: Long = 0
                while(!stop && System.nanoTime().also{remainingTime = (endTime - it) / 1000000} < endTime){
                    sleep(remainingTime.coerceAtMost(timestepMillis))
                }
            }
        } catch (e: SecurityException) {
            Log.e(BW_TAG, "How did this happen?  We checked permissions. ${e.cause}")
        }

    }

    override fun stopThread() {
        stop = true
    }

    companion object {
        private const val BW_TAG = "BLUETOOTH_WORKER"
        fun constructBluetoothWorker(
            ctx: Context,
            scanPeriodMillis: Long,
            scanActiveMillis: Long,
            timestepMillis: Long = 1000
        ): BluetoothWorker? {
            val bm = ctx.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
            val leScanner = bm.adapter.bluetoothLeScanner //?: return null
            if(leScanner == null){
                Log.d(BW_TAG, "Failed to get leScanner, is BT on?")
                return null
            }

            fun checkPerms(perm: List<String>): Boolean {
                return perm.all {
                    ActivityCompat.checkSelfPermission(
                        ctx,
                        it
                    ) == PackageManager.PERMISSION_GRANTED
                }
            }

            val permList = if (android.os.Build.VERSION.SDK_INT >= 31) {
                listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH
                )
            } else {
                listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH
                )
            }

            if (!checkPerms(permList)) {
                Log.w(BW_TAG, "No scan perms!  Exiting")
                return null
            }

            return BluetoothWorker(
                leScanner,
                scanPeriodMillis,
                scanActiveMillis,
                timestepMillis
            )

        }
    }
}