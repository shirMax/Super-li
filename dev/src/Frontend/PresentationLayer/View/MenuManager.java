package Frontend.PresentationLayer.View;

import Backend.ServiceLayer.SuppliersAndStockService;
import Frontend.PresentationLayer.Model.Controller;

public class MenuManager {
    private Menu menu;
    public static boolean stopRun;
    public MenuManager(SuppliersAndStockService sass){
        stopRun = false;
        Controller.getInstance().setService(sass);
        menu = new MainMenu();
    }

    public void run() {
        while(!stopRun)
            menu = menu.run();
    }

    public void Terminate(){
        menu = new CloseMenu();
        run();
    }


}
