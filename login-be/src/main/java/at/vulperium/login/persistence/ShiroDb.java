package at.vulperium.login.persistence;

import javax.inject.Qualifier;
import java.lang.annotation.*;

/**
 * Identifizierung der ShiroDB
 */
@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
public @interface ShiroDb {
}
