package Frontend.PresentationLayer.View;

import Frontend.PresentationLayer.ViewModel.VMCloseMenu;

public class CloseMenu implements Menu{
    private VMCloseMenu vmCloseMenu;
    public CloseMenu(){
        vmCloseMenu = new VMCloseMenu();
    }

    @Override
    public Menu run() {
        MenuManager.stopRun = true;
        return this;
    }

}
