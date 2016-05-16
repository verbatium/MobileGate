package ee.valja7.gate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

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

}
