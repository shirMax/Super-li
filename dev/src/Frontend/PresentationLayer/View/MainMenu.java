package Frontend.PresentationLayer.View;

import java.util.Scanner;

public class MainMenu implements Menu {
    MainMenu(){}


    @Override
    public Menu run() {
        return new SuppliersMenu();
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("---------suppliers main menu---------");
//        System.out.println("press 1 for suppliers menu");
//        System.out.println("press 2 for suppliers load data");
//        System.out.println("****press -1 to exit****");
//        String input = scanner.next();
//        switch (input) {
//            case "1":
//                return new SuppliersMenu();
//            case "2": {
//                new LoadData().loadData();
//                break;
//            }
//            case "-1":
//                MenuManager.stopRun = true;
//            default: System.out.println("invalid input");
//        }
//        return this;
    }
}
