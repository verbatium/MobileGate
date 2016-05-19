package ee.valja7.gate.persistence;

import ee.valja7.gate.Service;

import java.util.List;

public class UserService extends Service {
    public UserEntity findByUsername(String username) {
        return (UserEntity) getHibernate()
                .createQuery("from Users u where u.userName=:username")
                .setString("username", username)
                .uniqueResult();
    }

    public List<UsersEntity> getAll() {
        return getHibernate()
                .createQuery("from Users")
                .list();
    }

    public void save(UserEntity userEntity) {
        getHibernate().save(userEntity);
        getHibernate().flush();

    }
}
