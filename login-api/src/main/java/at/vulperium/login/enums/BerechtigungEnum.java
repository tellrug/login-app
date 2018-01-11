package at.vulperium.login.enums;

/**
 * Created by 02ub0400 on 10.07.2017.
 */
public enum BerechtigungEnum implements RolleUndBerechtigung {

    //TEST
    DASHBOARD("Dashboard"),
    USER_INFO("User-Info"),
    USER_NEUANLAGE("User-Neuanlage"),
    USER_BEARBEITUNG("User-Bearbeitung"),
    PROFIL_BEARBEITEN("Profil bearbeiten"),
    DNS_CHECK("DNS-Check"),
    WEB_LINKS("Web-Links");

    private String bezeichnung;

    BerechtigungEnum(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    @Override
    public String getBezeichnung() {
        return bezeichnung;
    }

    public static BerechtigungEnum getByBezeichnung(String bezeichnung) {
        for (BerechtigungEnum berechtigungEnum : values()) {
            if (berechtigungEnum.getBezeichnung().equals(bezeichnung)) {
                return berechtigungEnum;
            }
        }

        return null;
    }
}
