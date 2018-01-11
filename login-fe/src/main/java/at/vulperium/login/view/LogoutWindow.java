package at.vulperium.login.view;


import at.vulperium.fe.base.BaseStyles;
import at.vulperium.fe.base.ViewConstants;
import at.vulperium.fe.base.ViewId;
import at.vulperium.fe.base.components.SimpleWindowLayout;
import at.vulperium.fe.messagebundles.UtilityMessages;
import at.vulperium.login.LoginShiroService;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.*;
import org.apache.deltaspike.core.api.provider.BeanProvider;

/**
 * Created by 02ub0400 on 05.07.2017.
 */
public class LogoutWindow extends Window {

    private static final long serialVersionUID = 639431549543044551L;

    private LoginShiroService loginShiroService = BeanProvider.getContextualReference(LoginShiroService.class);
    private UtilityMessages utilityMessages = BeanProvider.getContextualReference(UtilityMessages.class);

    public LogoutWindow(String username) {
        initWindow(username);
    }

    private void initWindow(String username) {
        setHeight(ViewConstants.MIN_WIN_HEIGHT, Unit.PERCENTAGE);
        setWidth(ViewConstants.MIN_WIN_WIDTH, Unit.PERCENTAGE);
        setClosable(false);
        setModal(true);
        setDraggable(false);
        setResizable(false);
        setStyleName(BaseStyles.WINDOW_CAPTION);

        setContent(initLayout(username));
    }

    private Component initLayout(String username) {

        SimpleWindowLayout logoutLayout = new SimpleWindowLayout(utilityMessages.abmelden(), VaadinIcons.SIGN_OUT, utilityMessages.abmelden(), utilityMessages.abbrechen());

        Layout contentLayout = logoutLayout.getContentLayout();
        Label label = new Label("User " + username + " wirklich ausloggen?");
        contentLayout.addComponent(label);

        //Hinzufuegen von Click-Listener
        logoutLayout.addClickListenerToButton1((Button.ClickListener) event -> {
            close();
            loginShiroService.logout();
            //Zurueck zum Login
            UI.getCurrent().getPage().setLocation(ViewId.LOGIN);
        });
        logoutLayout.addClickListenerToButton2((Button.ClickListener) event -> close());

        return logoutLayout.getWindowLayout();
    }
}
