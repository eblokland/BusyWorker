package land.erikblok.busyworker.Workers.ABTests;

abstract public class AbstractABWorkload {
    protected int iterations;
    public AbstractABWorkload(int iterations){
        this.iterations = iterations;
    }

    public abstract void work();
}
