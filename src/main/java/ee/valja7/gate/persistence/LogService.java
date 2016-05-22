package ee.valja7.gate.persistence;

import ee.valja7.gate.Service;

import java.util.List;

public class LogService extends Service {
    public List<LogEntity> getAll() {
        return getHibernate()
                .createQuery("from Logs l order by l.id desc")
                .setMaxResults(20)
                .list();
    }
}
