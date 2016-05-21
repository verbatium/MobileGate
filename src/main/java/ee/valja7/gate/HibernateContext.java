package ee.valja7.gate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;

import java.util.concurrent.Callable;

public class HibernateContext {
    private static ThreadLocal<Session> context = new ThreadLocal<>();

    public static Session openSession() {
        if (context.get() != null)
            throw new IllegalStateException("Session already bound to this thread");
        Session session = getSessionFactory().openSession();
        context.set(session);
        return session;
    }

    private static SessionFactory getSessionFactory() {
        return Launcher.injector.getInstance(SessionFactory.class);
    }

    public static Session getSession() {
        Session session = context.get();
        if (session == null)
            throw new IllegalStateException("No session bound to this thread");
        return session;
    }

    public static void setSession(Session session) {
        if (context.get() != null)
            throw new IllegalStateException("Session already bound to this thread");
        context.set(session);
    }

    public static void closeSession() {
        Session session = context.get();
        if (session != null) {
            session.close();
            context.remove();
        }
    }

    public static <T> T doInTransaction(Callable<T> callable) {
        return doInTransaction(callable, false);
    }

    public static <T> T doInTransaction(Callable<T> callable, boolean flushAndClear) {
        if (getSession().getTransaction().getStatus() == TransactionStatus.ACTIVE) {
            try {
                return callable.call();
            } catch (Exception e) {
                throw runtimeException(e);
            }
        }
        Transaction transaction = getSession().beginTransaction();
        try {
            T result = callable.call();

            if (flushAndClear) {
                getSession().flush();
                getSession().clear();
            }

            transaction.commit();
            return result;
        } catch (Exception e) {
            throw runtimeException(e);
        } finally {
            if (transaction.getStatus() != TransactionStatus.COMMITTED)
                transaction.rollback();
        }
    }

    private static RuntimeException runtimeException(Exception e) {
        return e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
    }
}
