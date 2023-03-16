package land.erikblok.busyworker.Workers.ABTests.MemberIgnoringMethod;

import land.erikblok.busyworker.Workers.ABTests.AbstractABWorkload;
import land.erikblok.busyworker.Workers.AbstractWorkload;

abstract public class AbstractMemberIgnoringMethodWorkload extends AbstractABWorkload {
    int lonelyMember = 0;
    public AbstractMemberIgnoringMethodWorkload(int workAmount) {
        super(workAmount);
    }

}
