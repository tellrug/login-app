package at.vulperium.login.service;



import at.vulperium.login.dto.RolleDTO;
import at.vulperium.login.enums.RolleEnum;

import java.util.Map;

/**
 * Created by 02ub0400 on 12.09.2017.
 */
public interface UserManagerCacheService {

    Map<RolleEnum, RolleDTO> holeAlleRollen();

    Long holeIdZuRolle(RolleEnum rolleEnum);
}
