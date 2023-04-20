package land.erikblok.busyworker.Workers.ABTests.InternalSetter;

enum class InternalSetterWorkloadVariant {
    PRIVATE_INTERNAL_SETTER,
    FIXED_INTERNAL_SETTER,
    PUBLIC_INTERNAL_SETTER;

    companion object{
        private val values_cache = InternalSetterWorkloadVariant.values()
        fun intToWorkloadVariant(value: Int): InternalSetterWorkloadVariant{
            return values_cache[value]
        }

        fun strToWorkloadVariant(str: String): InternalSetterWorkloadVariant{
            for(value in values_cache){
              if(value.name == str) return value
            }
            throw java.lang.IllegalArgumentException("No matching value, received ${str}")
        }
    }
}
