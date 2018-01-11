package at.vulperium.login.view;



import at.vulperium.login.LoginConstants;
import at.vulperium.login.ViewId;
import at.vulperium.login.service.LoginShiroService;
import com.vaadin.annotations.Theme;
import com.vaadin.cdi.CDIUI;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Created by 02ub0400 on 20.01.2017.
 */
@Theme("logintheme")
@CDIUI(value = ViewId.LOGIN)
public class LoginUI extends UI {

    private static final long serialVersionUID = 2784795372762079504L;
    private static final Logger logger = LoggerFactory.getLogger(LoginUI.class);

    private @Inject
    LoginShiroService loginShiroService;

    @Override
    protected void init(VaadinRequest request) {
        Page.getCurrent().setTitle(getPageCaption());

        this.setId(ViewId.LOGIN);
        setContent(createCustomLayout());
    }

    private Component createCustomLayout() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setSpacing(false);
        mainLayout.setMargin(false);

        //Holen der BackUrl um auf ausgehende Seite nach erfolgtem Login zu leiten
        String backUrl = VaadinRequest.getCurrent().getParameter(LoginConstants.PARAM_BACKURL);

        Panel loginPanel = initLoginPanel();
        loginPanel.setContent(erstelleLoginLayout(backUrl));

        mainLayout.addComponent(loginPanel);
        mainLayout.setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER);
        return mainLayout;
    }

    private String getPageCaption() {
        return "Login";
    }


    private Panel initLoginPanel() {
        Panel loginPanel = new Panel("Login");
        loginPanel.setSizeUndefined();
        loginPanel.setWidth(30.0f, Sizeable.Unit.PERCENTAGE);

        return loginPanel;
    }

    private Component erstelleLoginLayout(final String backUrl) {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setMargin(new MarginInfo(true, true, true, true));

        Label label = new Label("Willkommen!");
        verticalLayout.addComponent(label);
        verticalLayout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);

        FormLayout flLoginLayout = new FormLayout();
        flLoginLayout.setSizeFull();

        final TextField tfUsername = new TextField("Benutzer:");
        tfUsername.setIcon(VaadinIcons.USER);
        tfUsername.setSizeFull();
        flLoginLayout.addComponent(tfUsername);


        final PasswordField tfPassword = new PasswordField("Passwort:");
        tfPassword.setIcon(VaadinIcons.KEY);
        tfPassword.setSizeFull();
        flLoginLayout.addComponent(tfPassword);

        verticalLayout.addComponent(flLoginLayout);

        Button pbLogin = new Button("Login");
        pbLogin.setIcon(VaadinIcons.UNLOCK);
        pbLogin.addClickListener((Button.ClickListener) clickEvent -> {
            boolean loginAccepted = loginShiroService.login(tfUsername.getValue(), tfPassword.getValue());
            if (loginAccepted) {

                //10 Minuten Inaktiv --> Session-Timeout
                VaadinService.getCurrentRequest().getWrappedSession().setMaxInactiveInterval(60 * 10);

                if (backUrl != null) {
                    //TODO wie BackUrl richtig benutzen? View wird nicht mitgeliefert
                    Page.getCurrent().setLocation(backUrl + "#!userinfo");
                }
                else {
                    //Zeige UserInfo an
                    Page.getCurrent().setLocation("vulperiumLogin/#!"+ ViewId.USER_INFO);
                }
            }
        });
        verticalLayout.addComponent(pbLogin);
        verticalLayout.setComponentAlignment(pbLogin, Alignment.MIDDLE_RIGHT);

        return verticalLayout;
    }
}
