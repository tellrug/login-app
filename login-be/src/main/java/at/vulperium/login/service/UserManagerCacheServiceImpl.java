package at.vulperium.login.service;

import at.vulperium.usermanager.dto.RolleDTO;
import at.vulperium.usermanager.enums.RolleEnum;
import at.vulperium.usermanager.service.RollenBerechtigungService;
import at.vulperium.usermanager.service.UserManagerCacheService;
import at.vulperium.util.base.caching.Cache;
import at.vulperium.util.base.caching.NamedCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 02ub0400 on 12.09.2017.
 */
@ApplicationScoped
public class UserManagerCacheServiceImpl implements UserManagerCacheService {

    public static final Logger logger = LoggerFactory.getLogger(UserManagerCacheServiceImpl.class);

    private static final String CACHE_KEY = "userManagerCacheKey";

    private @Inject RollenBerechtigungService rollenBerechtigungService;

    @Inject
    @NamedCache("usermanagercache")
    private Cache<String, UserManagerCache> cache;

    @PostConstruct
    public void postConstruct() {
        //Initialisierung des Caches
        UserManagerCache userManagerCache = new UserManagerCache();

        initRollenCache(userManagerCache);

        //Ablegen des Initialisierten Caches
        cache.put(CACHE_KEY, userManagerCache);
    }

    @Override
    public Map<RolleEnum, RolleDTO> holeAlleRollen() {
        UserManagerCache userManagerCache = getUserManagerCache();
        return userManagerCache.getRolleDTOMap();
    }

    @Override
    public Long holeIdZuRolle(RolleEnum rolleEnum) {
        UserManagerCache userManagerCache = getUserManagerCache();
        Map<RolleEnum, RolleDTO> rolleDTOMap = userManagerCache.getRolleDTOMap();

        RolleDTO rolleDTO = rolleDTOMap.get(rolleEnum);

        if (rolleDTO == null) {
            logger.error("Rolle={} ist nicht im Cache vorhanden!", rolleEnum);
            return null;
        }
        else {
            return rolleDTO.getId();
        }
    }

    private void initRollenCache(UserManagerCache userManagerCache) {
        Map<RolleEnum, RolleDTO> rolleDTOMap = rollenBerechtigungService.holeAlleRollen();
        userManagerCache.getRolleDTOMap().putAll(rolleDTOMap);
    }

    private UserManagerCache getUserManagerCache() {
        //Holen des Caches
        UserManagerCache userManagerCache = cache.get(CACHE_KEY);

        if (userManagerCache == null) {
            synchronized (cache) {
                userManagerCache = cache.get(CACHE_KEY);
                if (userManagerCache == null) {
                    postConstruct();
                    userManagerCache = cache.get(CACHE_KEY);
                }
            }
        }

        return userManagerCache;
    }


    private static final class UserManagerCache implements Serializable {

        private static final long serialVersionUID = 5745851648843113149L;
        private final Map<RolleEnum, RolleDTO> rolleDTOMap = new HashMap<>();


        public Map<RolleEnum, RolleDTO> getRolleDTOMap() {
            return rolleDTOMap;
        }
    }
}
