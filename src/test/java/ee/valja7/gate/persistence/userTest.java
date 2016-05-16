package ee.valja7.gate.persistence;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class userTest {
    @Test
    public void checkPassword() throws Exception {
        UserEntity userEntity = new UserEntity();
        userEntity.password = "$2a$12$vKiTbvhoX./O106y59Oev.pOy1/pkWHfb2XfqbQp.x9ygm2/gsczy";
        assertTrue(userEntity.checkPassword("admin"));
    }
}