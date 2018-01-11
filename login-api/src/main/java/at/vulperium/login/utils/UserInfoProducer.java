package at.vulperium.login.utils;

import at.vulperium.login.enums.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.inject.Named;
import java.util.Collections;

/**
 * Created by 02ub0400 on 28.09.2017.
 */
@ApplicationScoped
public class UserInfoProducer {

    private static final UserInfo ANONYM_USER = new UserInfo(-1L, "Anonymer User", "anonym", "anonym@mail.com", UserStatus.INAKTIV, Collections.emptySet(), Collections.emptySet());

    private static final Logger logger = LoggerFactory.getLogger(UserInfoProducer.class);

    private ThreadLocal<UserInfo> currentUserInfo = new ThreadLocal<>();

    public void setUserInfo(UserInfo userInfo) {
        currentUserInfo.set(userInfo);
    }

    public void clearUserInfo() {
        currentUserInfo.set(null);
        currentUserInfo.remove();
    }

    @Produces
    @RequestScoped
    @Typed(UserInfo.class)
    @Named("currentUser")
    public UserInfo createUserInfo() {
        UserInfo userInfo = currentUserInfo.get();

        //Fallback auf anonymen User
        if (userInfo == null) {
            logger.info("Dieser Thread verwendet den Anonymen User.");
            System.out.println("ANONYMER USER WURDE ABGEHOLT");
            return ANONYM_USER;
        }
        return userInfo;
    }
}
