package ee.valja7.gate;

import org.hibernate.LockOptions;
import org.hibernate.Session;

public class Service {


    protected Session getHibernate() {
        return HibernateContext.getSession();
    }

    public void lock(Object object) {
        getHibernate().buildLockRequest(LockOptions.UPGRADE).lock(object);
    }

}
