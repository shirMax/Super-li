package Frontend.PresentationLayer.View;

import Frontend.PresentationLayer.ViewModel.VMOrderMenu;

import java.util.Scanner;

public class OrderMenu implements Menu {
    VMOrderMenu vmOrderMenu;

    public OrderMenu() {
        vmOrderMenu = new VMOrderMenu();
    }

    @Override
    public Menu run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("---------orders menu---------");
        System.out.println("press 1 for send automatic collect order");
        System.out.println("press 2 for send automatic const order");
        System.out.println("press 3 for send const orders to next week");
        System.out.println("press 4 for cancel order");
        System.out.println("****press -1 to exit****");
        String input = scanner.next();
        switch (input) {
            case "1":
                return vmOrderMenu.orderCollect();
            case "2":
                return vmOrderMenu.orderConst();
            case "3": {
                vmOrderMenu.sendConstOrders();
                break;
            }
            case "4": {
                vmOrderMenu.cancelOrder();
                break;
            }
            case "-1": {
                MenuManager.stopRun = true;
                break;
            }
            default: System.out.println("invalid input");
        }
        return this;
    }
}
