package at.vulperium.login.service;

import at.vulperium.login.entities.RolleBerechtigung;
import at.vulperium.persistence.ShiroDb;
import at.vulperium.usermanager.dto.RolleDTO;
import at.vulperium.usermanager.enums.BerechtigungEnum;
import at.vulperium.usermanager.enums.RolleEnum;
import at.vulperium.usermanager.service.RollenBerechtigungService;
import at.vulperium.util.base.BaseUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by 02ub0400 on 12.09.2017.
 */
@ApplicationScoped
public class RollenBerechtigungServiceImpl implements RollenBerechtigungService {

    private @Inject @ShiroDb EntityManager em;

    @Override
    public Map<RolleEnum, RolleDTO> holeAlleRollen() {
        TypedQuery<RolleBerechtigung> query = em.createNamedQuery(RolleBerechtigung.QRY_FIND_ALL_ROLLE_BERECHTIGUNG, RolleBerechtigung.class);
        List<RolleBerechtigung> rolleBerechtigungList = query.getResultList();

        Map<RolleEnum, Set<BerechtigungEnum>> rolleBerechtigungMap = new HashMap<>();
        Map<RolleEnum, Long> rolleIdMap = new HashMap<>();
        for (RolleBerechtigung rolleBerechtigung : rolleBerechtigungList) {
            RolleEnum rolleEnum = RolleEnum.getByBezeichnung(rolleBerechtigung.getRolle().getBezeichnung());
            rolleIdMap.put(rolleEnum, rolleBerechtigung.getRolle().getId());
            BaseUtil.addToMapSet(rolleBerechtigungMap, rolleEnum).add(BerechtigungEnum.getByBezeichnung(rolleBerechtigung.getBerechtigung().getBezeichnung()));
        }

        //Umwandeln in RolleDTO
        return transformToRolleDTOMap(rolleIdMap, rolleBerechtigungMap);
    }


    private Map<RolleEnum, RolleDTO> transformToRolleDTOMap(Map<RolleEnum, Long> rolleIdMap, Map<RolleEnum, Set<BerechtigungEnum>> rolleBerechtigungMap) {

        Map<RolleEnum, RolleDTO> rolleDTOMap = new HashMap<>();
        if (rolleBerechtigungMap == null) {
            return rolleDTOMap;
        }

        for (RolleEnum rolleEnum : rolleBerechtigungMap.keySet()) {
            Long id = rolleIdMap.get(rolleEnum);
            RolleDTO rolleDTO = new RolleDTO(id, rolleEnum, rolleBerechtigungMap.get(rolleEnum));
            rolleDTOMap.put(rolleEnum, rolleDTO);
        }

        return rolleDTOMap;
    }
}
