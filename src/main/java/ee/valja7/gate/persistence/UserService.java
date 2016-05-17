package ee.valja7.gate.persistence;

import ee.valja7.gate.Service;

public class UserService extends Service {
    public UserEntity findByUsername(String username) {
        return (UserEntity) getHibernate()
                .createQuery("from Users u where u.userName=:username")
                .setString("username", username)
                .uniqueResult();
    }
}
