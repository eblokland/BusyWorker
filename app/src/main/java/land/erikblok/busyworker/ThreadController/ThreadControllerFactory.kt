package land.erikblok.busyworker.ThreadController

import android.content.Context
import android.content.Intent
import land.erikblok.busyworker.ThreadController.ABTesting.ForLoopThreadController
import land.erikblok.busyworker.ThreadController.ABTesting.ForLoopThreadController.Companion.ACTION_START_FORLOOP
import land.erikblok.busyworker.ThreadController.ABTesting.InternalSetterThreadController
import land.erikblok.busyworker.ThreadController.ABTesting.InternalSetterThreadController.Companion.ACTION_START_IS
import land.erikblok.busyworker.ThreadController.ABTesting.MemberIgnoringMethodThreadController
import land.erikblok.busyworker.ThreadController.ABTesting.MemberIgnoringMethodThreadController.Companion.ACTION_START_MIM
import land.erikblok.busyworker.ThreadController.BusyThreadController.Companion.ACTION_STARTBUSY
import land.erikblok.busyworker.ThreadController.RandomThreadController.Companion.ACTION_STARTRANDOM
import land.erikblok.busyworker.ThreadController.SensorThreadController.Companion.ACTION_START_SENSOR

class ThreadControllerFactory {
    fun getThreadControllerFromIntent(ctx: Context, intent: Intent): AbstractThreadController?{
        return when (intent.action){
            ACTION_STARTBUSY -> BusyThreadController.getControllerFromIntent(ctx, intent)
            ACTION_STARTRANDOM -> RandomThreadController.getControllerFromIntent(ctx, intent)
            ACTION_START_MIM -> MemberIgnoringMethodThreadController.getControllerFromIntent(ctx, intent)
            ACTION_START_IS -> InternalSetterThreadController.getControllerFromIntent(ctx, intent)
            ACTION_START_FORLOOP -> ForLoopThreadController.getControllerFromIntent(ctx, intent)
            ACTION_START_SENSOR -> SensorThreadController.getControllerFromIntent(ctx, intent)
            else -> null
        }
    }
}