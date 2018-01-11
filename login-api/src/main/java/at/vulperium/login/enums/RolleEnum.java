package at.vulperium.login.enums;

/**
 * Created by 02ub0400 on 10.07.2017.
 */
public enum RolleEnum implements RolleUndBerechtigung {

    //ACHTUNG - nur fuer Tests
    TEST_ADMINISTRATOR("TEST Administrator"),
    TEST_USER("TEST User"),
    TEST_GAST("TEST Gast");


    private String bezeichnung;

    RolleEnum(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    @Override
    public String getBezeichnung() {
        return bezeichnung;
    }

    public static RolleEnum getByBezeichnung(String bezeichnung) {
        for (RolleEnum rolleEnum : values()) {
            if (rolleEnum.getBezeichnung().equals(bezeichnung)) {
                return rolleEnum;
            }
        }

        return null;
    }
}
