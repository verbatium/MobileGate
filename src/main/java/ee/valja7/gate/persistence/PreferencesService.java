package ee.valja7.gate.persistence;

import ee.valja7.gate.Service;

import java.util.List;

public class PreferencesService extends Service {

    public List<PreferenceEntity> getAll() {
        return getHibernate()
                .createQuery("from PreferenceEntity")
                .list();
    }

    public List<PreferenceEntity> getByCategory(Categories category) {
        return getHibernate()
                .createQuery("from PreferenceEntity p where p.category=:category")
                .setString("category", category.name())
                .list();
    }

    public PreferenceEntity getByCategoryAndName(Categories category, String name) {
        return (PreferenceEntity) getHibernate()
                .createQuery("from PreferenceEntity p where p.category=:category and p.name=:name")
                .setString("category", category.name())
                .setString("name", name)
                .uniqueResult();
    }
}
