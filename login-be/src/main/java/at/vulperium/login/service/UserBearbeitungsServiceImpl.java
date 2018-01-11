package at.vulperium.login.service;

import at.vulperium.login.dto.UserBearbeitungsDTO;
import at.vulperium.login.dto.UserDatenDTO;
import at.vulperium.login.entities.Rolle;
import at.vulperium.login.entities.RolleBerechtigung;
import at.vulperium.login.entities.User;
import at.vulperium.login.entities.UserRolle;
import at.vulperium.login.enums.BerechtigungEnum;
import at.vulperium.login.enums.RolleEnum;
import at.vulperium.login.persistence.ShiroDb;
import at.vulperium.login.transformer.UserDTOTransformer;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;


/**
 * Created by 02ub0400 on 10.07.2017.
 */
@ApplicationScoped
@Transactional
public class UserBearbeitungsServiceImpl implements UserBearbeitungsService {

    public static final Logger logger = LoggerFactory.getLogger(UserBearbeitungsServiceImpl.class);
    public static final String SALT = "SALT";

    private @Inject @ShiroDb
    EntityManager em;

    private @Inject UserManagerCacheService userManagerCacheService;
    private @Inject UserDTOTransformer userDTOTransformer;

    @Override
    public UserDatenDTO holeUserDatenDTO(Long userId) {
        User user = em.find(User.class, userId);
        return userDTOTransformer.transform(user);
    }

    @Override
    public UserBearbeitungsDTO holeUserBearbeitungsDTO(Long userId) {
        UserDatenDTO userDatenDTO = holeUserDatenDTO(userId);
        return ermittleUserDTO(userDatenDTO);
    }


    @Override
    public UserDatenDTO holeUserDatenDTO(String username) {
        TypedQuery<User> query = em.createNamedQuery(User.QRY_FIND_USER_BY_USERNAME, User.class);
        query.setParameter(User.PARAM_USERNAME, username);

        User user = query.getSingleResult();
        return userDTOTransformer.transform(user);
    }

    @Override
    public UserBearbeitungsDTO holeUserBearbeitungsDTO(String username) {
        UserDatenDTO userDatenDTO = holeUserDatenDTO(username);
        return ermittleUserDTO(userDatenDTO);
    }

    @Override
    public List<UserBearbeitungsDTO> holeAlleUserBearbeitungsDTOs() {
        TypedQuery<User> query = em.createNamedQuery(User.QRY_FIND_ALL_USERS, User.class);
        List<User> userList = query.getResultList();

        List<UserBearbeitungsDTO> userBearbeitungsDTOList = new ArrayList<>();
        for (User user : userList) {
            UserDatenDTO userDatenDTO = userDTOTransformer.transform(user);
            UserBearbeitungsDTO userBearbeitungsDTO = ermittleUserDTO(userDatenDTO);
            userBearbeitungsDTOList.add(userBearbeitungsDTO);
        }

        return userBearbeitungsDTOList;
    }

    @Override
    public boolean checkPW(Long userId, String passwort) {
        Validate.notNull(userId, "UserId ist null.");
        Validate.notNull(passwort, "passwort ist null.");

        TypedQuery<User> query = em.createNamedQuery(User.QRY_CHECK_PW, User.class);
        query.setParameter(User.PARAM_USERID, userId);
        query.setParameter(User.PARAM_PW, passwort);

        boolean checkOK = true;
        try {
            query.getSingleResult();
        }
        catch (NoResultException nre) {
            logger.error("Fehlerhafte Passwort-Ueberpruefung bei UserId: {}", userId);
            checkOK = false;
        }
        catch (NonUniqueResultException rure) {
            logger.error("Keine eindeutige Passwort-Ueberpruefung bei UserId: {}", userId);
            checkOK = false;
        }

        return checkOK;
    }

    @Override
    public boolean aktualisierePW(Long userId, String altesPW, String neuesPW) {
        Validate.notNull(userId, "UserId ist null.");
        Validate.notNull(altesPW, "altesPW ist null.");
        Validate.notNull(neuesPW, "neuesPW ist null.");

        if (checkPW(userId, altesPW)) {
            User user = em.find(User.class, userId);
            user.setPasswort(neuesPW);
            return true;
        }

        return false;
    }

    @Override
    public Long legeNeuenUserAn(UserBearbeitungsDTO userBearbeitungsDTO, String passwort) {
        Validate.notNull(userBearbeitungsDTO, "userDTO ist null.");
        Validate.notNull(passwort, "passwort ist null.");
        Validate.notNull(userBearbeitungsDTO.getUserDatenDTO(), "userInfoDTO ist null.");
        Validate.notNull(userBearbeitungsDTO.getRollen(), "aktiveRollen ist null.");

        //Anlegen der UserInformationen
        UserDatenDTO userDatenDTO = userBearbeitungsDTO.getUserDatenDTO();
        if (userDatenDTO.getId() != null) {
            //User ist bereits vorhanden
            logger.error("Fehler beim Anlegen von User! User ist bereits vorhanden! UserId: {}", userDatenDTO.getId());
            throw new RuntimeException("Fehler beim Anlegen von User! User ist bereits vorhanden! UserId: " + userDatenDTO.getId());
        }

        User user = userDTOTransformer.transformInverse(userDatenDTO);
        user.setPasswort(passwort);

        em.persist(user);
        Long userId = user.getId();

        //Anlegen der aktiven Rollen
        aktualisiereAktivierteRollenFuerUser(user, userBearbeitungsDTO.getRollen());

        return userId;
    }

    @Override
    public UserBearbeitungsDTO aktualisiereUser(UserBearbeitungsDTO userBearbeitungsDTO, String passwort) {
        Validate.notNull(userBearbeitungsDTO, "userDTO ist null.");
        Validate.notNull(userBearbeitungsDTO.getUserDatenDTO(), "userInfoDTO ist null.");
        Validate.notNull(userBearbeitungsDTO.getRollen(), "aktiveRollen ist null.");

        //Anlegen der UserInformationen
        UserDatenDTO userDatenDTO = userBearbeitungsDTO.getUserDatenDTO();
        if (userDatenDTO.getId() == null) {
            //User ist nicht vorhanden
            logger.error("Fehler bei Aktualisierung von User! Keine UserId gesetzt!");
            throw new RuntimeException("Fehler bei Aktualisierung von User! Keine UserId gesetzt!");
        }

        User user = em.find(User.class, userDatenDTO.getId());
        if (user == null) {
            //User ist nicht vorhanden
            logger.error("Fehler bei Aktualisierung von User! User mit der UserId={} nicht vorhanden!", userDatenDTO.getId());
            throw new RuntimeException("Fehler bei Aktualisierung von User! User mit der UserId=" + userDatenDTO.getId() + " nicht vorhanden!");
        }

        //Ueberpruefen ob Aenderungen bei Userdaten vorgenommen werden
        User tmpUser = userDTOTransformer.transformInverse(userDatenDTO);
        if (passwort != null) {
            tmpUser.setPasswort(passwort);
        }
        if (istUserInfoAenderungVorhanden(user, tmpUser)) {
            user = userDTOTransformer.transformInverse(userDatenDTO, user);
            if (passwort != null) {
                user.setPasswort(passwort);
            }
            em.merge(user);
        }

        //Aktualisieren der Rollen
        aktualisiereAktivierteRollenFuerUser(user, userBearbeitungsDTO.getRollen());

        //User wieder transformieren
        return holeUserBearbeitungsDTO(user.getId());
    }

    @Override
    public String erzeugePasswortHash(String passwort) {
        Validate.notNull(passwort, "Passwort ist null.");
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(SALT.getBytes("UTF-8"));
            byte[] bytes = md.digest(passwort.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    private void aktualisiereAktivierteRollenFuerUser(User user, Set<RolleEnum> neueRollenSet) {
        //Ermitteln der bestehenden Rollen
        Map<RolleEnum, UserRolle> bestehendeRollenMap = ermittleRollenFuerUser(user.getId());
        Set<RolleEnum> bestehendeRollenSet = bestehendeRollenMap.isEmpty() ? new HashSet<>() : bestehendeRollenMap.keySet();

        //Abgleich der Rollen
        Pair<Set<RolleEnum>, Set<RolleEnum>> rollenAbgleichPair = gleicheRollenAb(bestehendeRollenSet, neueRollenSet);

        //Neu aktivierte Rollen anlegen
        legeRolleFuerUserAn(user, rollenAbgleichPair.getRight());

        //Deaktivierte Rollen loeschen
        entferneRolleFuerUser(user.getId(), rollenAbgleichPair.getLeft(), bestehendeRollenMap);
    }

    private boolean legeRolleFuerUserAn(User user, Set<RolleEnum> neueRollenSet) {
        for (RolleEnum rolleEnum : neueRollenSet) {
            Long rolleId = userManagerCacheService.holeIdZuRolle(rolleEnum);
            if (rolleId != null) {
                Rolle rolle = em.find(Rolle.class, rolleId);

                if (rolle == null) {
                    logger.error("Rolle mit der RolleId={} konnte in DB nicht gefunden werden!", rolleId);
                    continue;
                }

                UserRolle userRolle = new UserRolle();
                userRolle.setUser(user);
                userRolle.setRolle(rolle);

                em.persist(userRolle);
            }
        }

        return true;
    }

    private boolean entferneRolleFuerUser(Long userId, Set<RolleEnum> deaktivierteRollenSet, Map<RolleEnum, UserRolle> userRolleMap) {
        for (RolleEnum rolleEnum : deaktivierteRollenSet) {
            UserRolle userRolle = userRolleMap.get(rolleEnum);
            if (userRolle != null) {
                logger.info("Dem User mit der UserId={} wird die Rolle={} entzogen.", userRolle.getUser().getId(), rolleEnum.getBezeichnung());
                em.remove(userRolle);
            }
            else {
                logger.error("Rolle={} konnte dem User mit der UserId={} nicht entzogen werden! User besitzt diese Rolle nicht!",
                        rolleEnum.getBezeichnung(), userId);
            }
        }

        return true;
    }

    private UserBearbeitungsDTO ermittleUserDTO(UserDatenDTO userDatenDTO) {
        if (userDatenDTO != null) {
            //Ermitteln der Rollen
            Map<RolleEnum, UserRolle> userRolleMap = ermittleRollenFuerUser(userDatenDTO.getId());
            Set<RolleEnum> rollen = userRolleMap.isEmpty() ? new HashSet<>() : userRolleMap.keySet();
            //Ermitteln der Berechtigungen
            Set<BerechtigungEnum> berechtigungen = ermittleBerechtigungenFuerRollen(rollen);
            return new UserBearbeitungsDTO(userDatenDTO, rollen, berechtigungen);
        }
        return null;
    }

    private Map<RolleEnum, UserRolle> ermittleRollenFuerUser(Long userId) {
        TypedQuery<UserRolle> query = em.createNamedQuery(UserRolle.QRY_FIND_ROLLE_BY_USERID, UserRolle.class);
        query.setParameter(UserRolle.QRY_PARAM_USERID, userId);

        Map<RolleEnum, UserRolle> userRolleMap = new HashMap<>();

        List<UserRolle> result = query.getResultList();
        for (UserRolle userRolle : result) {
            RolleEnum rolle = RolleEnum.getByBezeichnung(userRolle.getRolle().getBezeichnung());
            if (rolle == null) {
                //Fehler!
                throw new RuntimeException("FEHLER! - Tabelle ROLLE ist mit ENUM nicht konsistent!");
            }
            userRolleMap.put(rolle, userRolle);
        }

        return userRolleMap;
    }

    private Set<BerechtigungEnum> ermittleBerechtigungenFuerRollen(Set<RolleEnum> rollen) {
        Set<String> rollenBezeichnungSet = new HashSet<>();
        for (RolleEnum rolleEnum : rollen) {
            rollenBezeichnungSet.add(rolleEnum.getBezeichnung());
        }

        TypedQuery<RolleBerechtigung> query = em.createNamedQuery(RolleBerechtigung.QRY_FIND_BERECHTIGUNG_BY_ROLLEN_BEZEICHNUNGEN, RolleBerechtigung.class);
        query.setParameter(RolleBerechtigung.QRY_PARAM_ROLLEN_BEZEICHNUNGEN, rollenBezeichnungSet);
        return verarbeiteRollenBerechtigungResult(query.getResultList());
    }

    private Set<BerechtigungEnum> ermittleBerechtigungenFuerRollenIds(Set<Long> rollenIds) {
        TypedQuery<RolleBerechtigung> query = em.createNamedQuery(RolleBerechtigung.QRY_FIND_BERECHTIGUNG_BY_ROLLE_IDS, RolleBerechtigung.class);
        query.setParameter(RolleBerechtigung.QRY_PARAM_ROLLEIDS, rollenIds);
        return verarbeiteRollenBerechtigungResult(query.getResultList());
    }


    private Set<BerechtigungEnum> verarbeiteRollenBerechtigungResult(List<RolleBerechtigung> rolleBerechtigungList) {
        Set<BerechtigungEnum> berechtigungen = new HashSet<>();
        for (RolleBerechtigung rolleBerechtigung : rolleBerechtigungList) {
            BerechtigungEnum berechtigung = BerechtigungEnum.getByBezeichnung(rolleBerechtigung.getBerechtigung().getBezeichnung());
            if (berechtigung == null) {
                //Fehler!
                throw new RuntimeException("FEHLER! - Tabelle BERECHTIGUNG ist mit ENUM nicht konsistent!");
            }
            berechtigungen.add(berechtigung);
        }

        return berechtigungen;
    }

    private Pair<Set<RolleEnum>, Set<RolleEnum>> gleicheRollenAb(Set<RolleEnum> bestehendeRollenSet, Set<RolleEnum> neueRollenSet) {
        Set<RolleEnum> zuLoeschendeRollenSet = new HashSet<>(bestehendeRollenSet);
        zuLoeschendeRollenSet.removeAll(neueRollenSet);

        Set<RolleEnum> rollenHinzufuegenSet = new HashSet<>(neueRollenSet);
        rollenHinzufuegenSet.removeAll(bestehendeRollenSet);

        return Pair.of(zuLoeschendeRollenSet, rollenHinzufuegenSet);
    }

    private boolean istUserInfoAenderungVorhanden(User user, User tmpUser) {
        return !StringUtils.equals(user.getUsername(), tmpUser.getUsername()) ||
                !StringUtils.equals(user.getName(), tmpUser.getName()) ||
                !StringUtils.equals(user.getEmail(), tmpUser.getEmail()) ||
                !StringUtils.equals(user.getPasswort(), tmpUser.getPasswort()) ||
                user.getAktiv() != tmpUser.getAktiv();
    }
}
