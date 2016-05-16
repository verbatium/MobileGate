package ee.valja7.gate;

import java.util.Set;

public interface Principal {
    boolean checkPassword(String password);

    String getDisplayName();

    String getUserName();

    Set<String> getRoles();
}
