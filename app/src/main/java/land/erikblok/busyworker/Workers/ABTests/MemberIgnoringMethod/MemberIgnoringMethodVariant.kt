package land.erikblok.busyworker.Workers.ABTests.MemberIgnoringMethod;

enum class MemberIgnoringMethodVariant {
    UNFIXED,
    FIXED;

    companion object{
        private val values_cache = MemberIgnoringMethodVariant.values()
        fun intToWorkloadVariant(value: Int): MemberIgnoringMethodVariant{
            return values_cache[value]
        }

        fun strToWorkloadVariant(str: String): MemberIgnoringMethodVariant{
            for(value in values_cache){
                if(value.name == str) return value
            }
            throw java.lang.IllegalArgumentException("No matching value, received ${str}")
        }
    }
}
