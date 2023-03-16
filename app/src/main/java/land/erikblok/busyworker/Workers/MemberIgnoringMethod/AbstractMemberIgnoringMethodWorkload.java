package land.erikblok.busyworker.Workers.MemberIgnoringMethod;

import land.erikblok.busyworker.Workers.AbstractWorkload;

abstract public class AbstractMemberIgnoringMethodWorkload implements AbstractWorkload {
    final int workAmount;
    int lonelyMember = 0;
    public AbstractMemberIgnoringMethodWorkload(int workAmount) {
        this.workAmount = workAmount;
    }

    public abstract void work();
}
