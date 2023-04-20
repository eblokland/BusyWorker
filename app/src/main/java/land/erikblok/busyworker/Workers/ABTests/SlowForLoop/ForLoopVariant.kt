package land.erikblok.busyworker.Workers.ABTests.SlowForLoop;

import land.erikblok.busyworker.Workers.ABTests.MemberIgnoringMethod.MemberIgnoringMethodVariant

enum class ForLoopVariant {
    UNFIXED,
    FIXED;

    companion object{
        private val values_cache = ForLoopVariant.values()
        fun intToWorkloadVariant(value: Int): ForLoopVariant {
            return values_cache[value]
        }

        fun strToWorkloadVariant(str: String): ForLoopVariant {
            for(value in values_cache){
                if(value.name == str) return value
            }
            throw java.lang.IllegalArgumentException("No matching value, received ${str}")
        }
    }
}
