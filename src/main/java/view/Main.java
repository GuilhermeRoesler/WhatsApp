package view;

import utils.Constants;
import utils.FileManager;
import utils.UserPreferences;

public class Main {
    public static void main(String[] args) {
        // initialize files
        FileManager.initializeFiles();

        // initialize frames
        Constants.frameLogin = new LoginJFrame();
        Constants.framePrincipal = new PrincipalJFrame();

        Constants.framePrincipal.setLocationRelativeTo(null);
        Constants.framePrincipal.setVisible(true);
        Constants.framePrincipal.setIconImage(Constants.LOGO);

        if (UserPreferences.isUserNull()) {
            Constants.framePrincipal.loginButtonClick();
        }
    }
}
// criptografia ponta-a-ponta
// autenticação por token JWT
// private static Map<String, List<Message>> userMessages = new HashMap<>();

/*
 * [13:34, 07/12/2024] Guilherme: Serve Not respondi g shut down
 * [13:34, 07/12/2024] Guilherme: Formatting phone
 * [13:34, 07/12/2024] Guilherme: SQL Phone unique
 * [13:35, 07/12/2024] Guilherme: SQL Phone primary key
 * [13:35, 07/12/2024] Guilherme: SQL order fix
 * [13:39, 07/12/2024] Guilherme: Format name capitalize
 * [16:02, 07/12/2024] Guilherme: Change maven names
 * [16:03, 07/12/2024] Guilherme: Update contact list
 * [16:04, 07/12/2024] Guilherme: New Unknown message appear for nothing
 * [16:04, 07/12/2024] Guilherme: ConversationId number
 * [16:08, 07/12/2024] Guilherme: Load contact list
 */