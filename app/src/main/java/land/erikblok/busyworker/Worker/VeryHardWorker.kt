package land.erikblok.busyworker.Worker

val TAG = "VERYHARDWORKER"
class VeryHardWorker() : AbstractWorker() {
    @Volatile var stop = false


    override fun run() {
        priority = MAX_PRIORITY
        var idontmatter : Long = 0
        //how long does it take to overflow this i wonder
        //maybe this does more math and less hitting ram
        while(/*(idontmatter % 100000 == 0.toLong()) &&*/ !stop){
           // idontmatter = idontmatter * idontmatter + idontmatter
            workload1()
            workload2()
        }
    }

    //workload 1 will run for 100k iterations
    private fun workload1(){
        var meaningless: Long = 0
        for (i in 1..100000){
            meaningless += 1
        }
    }
    //Workload 2 will run for a million iterations
    private fun workload2(){
        var meaningless: Long = 0
        for(i in 1..1000000){
            meaningless += 1
        }
    }
    override fun stopThread(){
        stop = true
    }
}