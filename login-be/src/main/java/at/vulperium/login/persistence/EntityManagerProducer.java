package at.vulperium.login.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 * INFO:
 * Achtung: das ist eine Persistence Unit mit JTA enabled!
 * Auf diesem EntityManager darf kein em.getTransaction().begin/commit() ausgefuehrt werden
 * Stattdessen muss eine transaction ueber @Resource UserTransaction (in EJB oder CDI beans) bezogen werden.
 * Dies passiert entweder in Stateless/Stateful EJBs bzw. mit dem DeltaSpike &#064;Transactional
 */
@RequestScoped
public class EntityManagerProducer {

    private static final Logger logger = LoggerFactory.getLogger(EntityManagerProducer.class);

    @PersistenceUnit(unitName = "SHIRO")
    private EntityManagerFactory emfShiro;


    @Produces
    @RequestScoped
    @ShiroDb
    public EntityManager createShiroEntityManager() {
        return emfShiro.createEntityManager();
    }

    public void closeShiroEm(@Disposes @ShiroDb EntityManager entityManager) {
        if (entityManager == null) {
            logger.warn("Shiro EntityManager dispose called with null EntityManager!");
            return;
        }
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }
}
