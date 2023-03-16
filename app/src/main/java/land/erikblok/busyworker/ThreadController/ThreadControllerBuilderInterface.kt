package land.erikblok.busyworker.ThreadController

import android.content.Context
import android.content.Intent

interface ThreadControllerBuilderInterface<T> {
    fun getControllerFromIntent(ctx: Context, intent: Intent): T?
}