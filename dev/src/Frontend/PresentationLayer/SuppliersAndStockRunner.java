package Frontend.PresentationLayer;

import Backend.ServiceLayer.Response;
import Backend.ServiceLayer.Service;
import Frontend.PresentationLayer.View.MenuManager;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class SuppliersAndStockRunner {

    Service mainService;

    public void start(){
        System.out.println("Welcome to Stock and Suppliers!");
//        String branch = getBranch();
//        if(branch == null)
//            return;
        Service mainService = new Service();
        mainService.initial();
        SMSRunner sms = new SMSRunner(mainService);
        //suppliers runner
        MenuManager mm = new MenuManager(mainService);
        int input = -1;
        while(input != 0){
            mm.stopRun = false;
            sms.setShouldStopMainLoop(false);
            printWelcomeMessage();
            input = takeInt(0,2);
            if(input == 1) {
                mm = new MenuManager(mainService);
                mm.run();
            }
            if(input == 2)
                sms.start();
            if(input == 0){
                sms.shutDown();
                mm.Terminate();
                System.out.println("thanks for using our system!");
            }

        }
    }

    private String getBranch() {
        mainService = new Service();
        List<String> branches = mainService.getAllBranches().Value;
        String branchAddress = "";
        boolean shouldStop = false;
        while(!shouldStop) {
            String branch = getStringInput("please choose branch: \n" +
                    String.join("\n", branches)+"\n\n"+
                    "NEW for a new branch\n" +
                    "type EXIT to close the system");
            if (branch.equalsIgnoreCase("NEW")){
                String address =  getStringInput("Please type branch address.");
                String area = getStringInput("please type branch area");
                Response res = mainService.addNewBranch(address,area);
                if(res.isErrorOccurred()){
                    System.out.println(res.errorMessage);
                    continue;
                }
                branchAddress = address;
                shouldStop = true;
            }

            else if (getBranchSystemName(branch,branches) != null){
                branchAddress = branch;
                shouldStop = true;
            }

            else if (branch.equalsIgnoreCase("EXIT")){
                branchAddress = null;
                shouldStop = true;
            }

            else System.out.println("please choose a branch from the list.");
        }
        mainService.shutDown();
        return branchAddress;
    }

    private String getBranchSystemName(String branch, List<String> branches) {
        for(String b : branches){
            if(branch.equalsIgnoreCase(b))
                return b;
        }
        return null;
    }


    private String getStringInput() {
        System.out.print("please type here: ");
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

    private String getStringInput(String msg) {
        System.out.println(msg);
        return getStringInput();
    }

    private void printWelcomeMessage(){
        System.out.println("\n" +
                "---Suppliers and Stock Menu---\n" +
                "for Suppliers please type 1\n" +
                "for Stock please type 2\n"+
                "to Shut down the system please type 0\n"+
                "\n");
    }

    private int takeInt(int start,int end){
        int i = takeInt();
        while(i < start || i > end){
            System.out.println("Invalid Input. please try again");
            i = takeInt();
        }
        return i;
    }

    private int takeInt(){
        boolean wentToCatch;
        int input = 0;
        Scanner sc = new Scanner(System.in);
        do {
            try {
                System.out.print("please type here: ");
                wentToCatch = false;
                input = sc.nextInt(); // sc is an object of scanner class
            } catch (InputMismatchException e) {
                sc.next();
                wentToCatch = true;
                System.out.println("Invalid Input. please try again");
            }
        } while (wentToCatch);
        return input;
    }
}
