package utils;

import java.awt.Image;
import java.io.File;
import java.util.function.Function;

import javax.swing.ImageIcon;

import view.LoginJFrame;
import view.PrincipalJFrame;

public class Constants {
    // URL do servidor
    public static final String ROOT_URL = "http://192.168.1.10:8080";
    public static final String SEND_DATA_URL = ROOT_URL + "/api/whatsapp/sendObject";
    public static final String LOGIN_URL = ROOT_URL + "/api/whatsapp/auth/login";
    public static final String REGISTER_URL = ROOT_URL + "/api/whatsapp/auth/register";
    public static final String CONTACT_VERIFICATION_URL = ROOT_URL + "/api/whatsapp/auth/verifyContact";

    // Path
    public static final String APPDATA = System.getenv("APPDATA");
    public static final String APP_DIR = APPDATA + File.separator + "WhatsApp";
    public static final String PREFS_FILE = APP_DIR + File.separator + "userconfig.properties";
    public static final String MESSAGES_DIR = APP_DIR + File.separator + "messages";

    // Files
    public static final String CONTACTS_FILE = APP_DIR + File.separator + "contacts.properties";
    public static final Function<String, String> GET_MESSAGE = conversationId -> (MESSAGES_DIR + File.separator + conversationId + ".properties");

    // Frames
    public static PrincipalJFrame framePrincipal;
    public static LoginJFrame frameLogin;

    // images
    public static final ImageIcon LOGO_ICON = ImageLoader.loadImage("whatsapp-logo.png");
    public static final Image LOGO = LOGO_ICON.getImage();
    public static final ImageIcon WPP_IMAGE_ICON = ImageLoader.loadImage("whatsapp2.png");
    public static final ImageIcon USER_PIC = ImageLoader.loadImage("default_profile_pic.png");
}
