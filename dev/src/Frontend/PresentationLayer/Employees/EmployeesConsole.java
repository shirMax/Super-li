package Frontend.PresentationLayer.Employees;

import Backend.BusinessLayer.Employees.Positions;
import Backend.BusinessLayer.Employees.ShiftType;
import Backend.PersistenceLayer.CreateDB;
import Backend.PersistenceLayer.DeliveriesDal.CreateDBDeliveries;
import Backend.PersistenceLayer.EmployeesDal.CreateDBEmployees;
import Backend.ServiceLayer.ObjectsEmployees.Result;
import Backend.ServiceLayer.ObjectsEmployees.SEmployee;
import Backend.ServiceLayer.ObjectsEmployees.SShift;
import Backend.ServiceLayer.ObjectsEmployees.ServiceEmployees;
import Backend.ServiceLayer.Service;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class EmployeesConsole {
    //private ServiceEmployees serviceControl;
    private Service serviceControl;
    private Scanner scn;
    public EmployeesConsole(Scanner scn, Service service) {
        //initDB();
        //serviceControl=new ServiceEmployees();
        this.scn = scn;
        this.serviceControl = service;
    }

    private void initDB() {
        CreateDB createDBEmployees = new CreateDBEmployees();
        createDBEmployees.createFileIfNotExists();
    }

    public void run(){
        boolean terminate=false;
        while(!terminate){
            ;  // Create a Scanner object
            System.out.println("enter:\n <1> for employees menu \n <2> for shift menu\n <3> for All notifications\n <end> to end the program");
            String cmd = scn.nextLine();  // Read user input
            switch(cmd){
                case "end":terminate=true;break;
                case "1":employeeMenu();break;
                case "2":shiftMenu();break;
                case "3":NotificationsMenu();break;
            }
        }
    }

    private void NotificationsMenu() {
        System.out.println("Notifications:");

           Result< Collection<String>> res = serviceControl.getAllHRNotifications();
           if(res.isError()) System.out.println(res.getMessage());
           else{
               for(String msg : res.getValue()){
                   System.out.println(msg+"\n");
               }
           }

    }

    private void shiftMenu() {
        System.out.println("Shift Menu:");
        System.out.println("enter:\n <1> print all shifts\n <2> add shift\n <3> add/remove employee to shift \n" +
                " <4> print employees available for a shift\n <5> remove shift\n");
        String cmd = scn.nextLine();
        switch (cmd) {
            case "1": {
                printAllShifts();
                break;
            }
            case "2" : {
                addShift();
                break;
            }
            case "3" : {
                addRemoveEmployeeToShift();
                break;
            }
            case "4" : {
                printAvailableEmployeesForShift();
                break;
            }
            case "5" : {
                removeShift();
                break;
            }
        }

    }

    private void removeShift() {
        System.out.println("to remove a shift use the following syntax:\n" +
        "<shift date>,<shift type (morning/evening index)>\n" +
                "morning - 0 , evening - 1\n" +
                "example: to remove an existing shift in friday morning in 29/4/22 use the command:\n" +
                "29-04-2022,0\n");
        String cmd = scn.nextLine();
        String[] args = cmd.split(",");
        if(args.length!=2) {System.out.println("missing arguments");return;}
        try{
            Date date = toDate(args[0]);
            String type="";
            if(args[1].equals("0"))type=ShiftType.morning();
            else if(args[1].equals("1"))type=ShiftType.evening();
            Result<SShift> res= serviceControl.removeShift(date,type);
            if(res.isError()) System.out.println(res.getMessage());
            else{
                System.out.println("Shift successfully removed:\n"+res.getValue());
            }
        }
        catch (Exception e){
            System.out.println("Syntax Error");
        }

    }

    private void printAvailableEmployeesForShift() {
        System.out.println("to print all available employees for a shift use the following syntax:\n" +
                "<day in week>,<morning/evening index>\n" +
                "morning - 0 , evening - 1\n" +
                "day in week : sunday - 0 , monday - 1 ,..., saturday - 6\n" +
                "example: to print all available employees for saturday morning use the command:\n" +
                "7,0\n");
        String cmd = scn.nextLine();
        String[] args = cmd.split(",");
        try{
            int day = Integer.parseInt(args[0]);
            String type = "";
            if(args[1].equals("0"))type = ShiftType.morning();
            else if(args[1].equals("1"))type = ShiftType.evening();
            Result<List<SEmployee>> res = serviceControl.getEmployeesForShift(day,type);
            if(res.isError()) System.out.println(res.getMessage());
            else{
                System.out.println("Employees available:");
                for(SEmployee s : res.getValue()) System.out.println(s);
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void addRemoveEmployeeToShift() {
        System.out.println("in order to add/remove an employee to an existing shift use the following syntax:\n" +
                "<shift date>,<add/remove index>,<morning/evening index>,<employee id>,<branch>\n" +
                "add - 0 , remove - 1\n" +
                "morning - 0 , evening - 1\n" +
                "example: to add employee gabi with id 206421745 to morning shift in 29/6/22 in tlv branch:\n" +
                "29-06-2022,0,0,206421745,tlv\n");
        String cmd = scn.nextLine();
        String[] args = cmd.split(",");
        if(args.length!=5) {System.out.println("missing arguments");return;}
        try{
            Date date = toDate(args[0]);
            int addRemove = Integer.parseInt(args[1]);
            int id = Integer.parseInt(args[3]);
            String type="";
            String branch = args[4];
            if(args[2].equals("0"))type=ShiftType.morning();
            else if(args[2].equals("1"))type=ShiftType.evening();
            if(addRemove==0){
                Result<Boolean> res = serviceControl.addEmployeeToShift(id,date,type,branch);
                if(res.isError()) System.out.println(res.getMessage());
                else System.out.println("employee successfully added");
            }
            else if(addRemove==1){
                Result<Boolean> res = serviceControl.removeEmployeeFromShift(id,date,type,branch);
                if(res.isError()) System.out.println(res.getMessage());
                else System.out.println("employee successfully removed");
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    private void addShift() {
        System.out.println("to add a shift use the following syntax:\n" +
                "<shift date>,<shift type (morning/evening index)>\n" +
                "morning - 0 , evening - 1\n" +
                "example: to create a new morning shift in tlv branch in 29/6/22 use the command:\n" +
                "29-06-2022,0,tlv\n");
        String cmd = scn.nextLine();
        String[] args = cmd.split(",");
        if(args.length!=3) {System.out.println("missing arguments");return;}
        try{
            Date date = toDate(args[0]);
            String type="";
            if(args[1].equals("0"))type=ShiftType.morning();
            else if(args[1].equals("1"))type=ShiftType.evening();
            String branch = args[2];
            Result<SShift> res= serviceControl.addShift(date,type,branch);
            if(res.isError()) System.out.println(res.getMessage());
            else{
                System.out.println("Shift successfully created:\n"+res.getValue());
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void printAllShifts() {
        Result<List<SShift>> l = serviceControl.getAllShifts();
        if(l.isError()) System.out.println(l.getMessage());
        else{
            System.out.println("Shifts:");
            for(SShift s : l.getValue()) System.out.println(s);
        }
    }

    private void employeeMenu() {
        System.out.println("Employee Menu:");
        System.out.println("enter:\n <1> print all employees\n <2> add new employee\n <3> remove employee\n <4> modify employee\n <5> modify employee availability");
        String cmd = scn.nextLine();
        switch (cmd) {
            case "1":{ printAllEmployees();break;}
            case "2":{ addNewEmployee();break;}
            case "3":{ removeEmployee();break;}
            case "4":{ modifyEmployee();break;}
            case "5":{ addRemoveTimeConstraint();break;}
        }

    }

    private void addRemoveTimeConstraint() {
        System.out.println("to add/remove employee's availability to work certain a shift use the following syntax:\n" +
                "<add/remove index>,<shift type(morning/evening) index>,<day in week index>,<employee id>\n" +
                "add - 0 , remove - 1\n" +
                "morning -0 , evening - 1\n" +
                "days in week : sunday -0 , monday -1 , .... saturday - 6\n" +
                "example: in order to make employee gabi with id 206421745 available to work in friday evenings use the command:\n" +
                "0,1,6,206421745\n");
        String cmd = scn.nextLine();
        String[] args = cmd.split(",");
        if(args.length!=4){ System.out.println("missing arguments");return;}
        try{
            int addRemove = Integer.parseInt(args[0]);
            String shiftType="";
            int type = Integer.parseInt(args[1]);
            if(type==0)shiftType= ShiftType.morning();
            else if(type==1)shiftType=ShiftType.evening();
            int day = Integer.parseInt(args[2]);
            int id = Integer.parseInt(args[3]);
            if(addRemove==0){
                Result<Boolean> res = serviceControl.addConstraint(day,shiftType,id);
                if(res.isError()) System.out.println(res.getMessage());
                else System.out.println("successfully executed");
            }
            else if(addRemove==1){
                Result<Boolean> res = serviceControl.removeConstraint(day,shiftType,id);
                if(res.isError()) System.out.println(res.getMessage());
                else System.out.println("successfully executed");
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void modifyEmployee() {
        System.out.println("Modify Employee:\nenter:\n<1> add/remove position to employee\n<2> update employee propety");
        String cmd=scn.nextLine();
        switch (cmd){
            case "1" :{ addRemovePosition();break;}
            case "2" :{ updateEmployeeProperty();break;}
        }
    }

    private void updateEmployeeProperty() {
        System.out.println("to update an employee's property use the following syntax:\n" +
                "<property index>,<new value>,<employee id>\n" +
                "Properties: name - 0 , salary - 1 , bank account - 2\n" +
                "example: to change employee's name to gabi (his id is 206421745) use the command:\n" +
                "0,gabi,206421745\n");
        String cmd = scn.nextLine();
        String[] args = cmd.split(",");
        if(args.length!=3) {System.out.println("missing arguments");return;}
        try{
            int prop = Integer.parseInt(args[0]);
            switch (prop) {
                case 0 : {
                    updateEmployeeName(args[1], args[2]);
                    break;
                }
                case 1 : {
                    updateEmployeeSalary(args[1], args[2]);
                    break;
                }
                case 2 : {
                    updateEmployeeBankAccount(args[1], args[2]);
                    break;
                }
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }



    private void updateEmployeeBankAccount(String bankAcc, String id) {
        int _id =Integer.parseInt(id);
        int _bankAcc = Integer.parseInt(bankAcc) ;
        Result<Boolean> res = serviceControl.UpdateEmployeeBankAccount(_id,_bankAcc);
        if(res.isError()) System.out.println(res.getMessage());
        else System.out.println("property successfully changed");
    }

    private void updateEmployeeSalary(String salary, String id) {
        int _id =Integer.parseInt(id);
        int _salary = Integer.parseInt(salary) ;
        Result<Boolean> res = serviceControl.UpdateEmployeeSalary(_id,_salary);
        if(res.isError()) System.out.println(res.getMessage());
        else System.out.println("property successfully changed");
    }

    private void updateEmployeeName(String name, String id) {
        int _id =Integer.parseInt(id);
        Result<Boolean> res = serviceControl.UpdateEmployeeName(_id,name);
        if(res.isError()) System.out.println(res.getMessage());
        else System.out.println("property successfully changed");
    }

    private void addRemovePosition() {
        System.out.println("to add/remove a position to an employee enter a command using the syntax:\n" +
                "<add/remove index>,<position index>,<employee id>\n" +
                "add - 0 , remove - 1\n" +
                "Positions:\n" +
                "cashier - 0 , stock keeper - 1 , mover - 2 , driver - 3, shift manager - 4 , \n" +
                "human resources manager - 5 \n" +
                "Licenses:\n"+
                "B - 0 , C - 1 , C1 - 2, CE - 3\n"+
                "if you are willing to add the driver position, a driver's license must be provided as well\n"+
                "to add the Driver position the syntax is:\n"+
                "<add/remove index>,<position index>,<employee id>,<license index>\n"+
                "example: to add employee with id 206421745 the position mover you should enter the command:\n" +
                "0,2,206421745\n" );
        String cmd = scn.nextLine();
        String[] args = cmd.split(",");
        if( (args.length!=3) &(args.length!=4)){System.out.println("missing arguments");return;}
        try{
            if(args.length==3) {
                int addRemove = Integer.parseInt(args[0]);
                int pos = Integer.parseInt(args[1]);
                int id = Integer.parseInt(args[2]);
                if(pos==3){
                    System.out.println("License missing!");
                    return;
                }
                Result<Boolean> res = handleAddRemove(addRemove, pos, id);
                if (res.isError()) System.out.println(res.getMessage());
                else System.out.println("successfully executed");
            }
            else{
                int addRemove = Integer.parseInt(args[0]);
                int pos = Integer.parseInt(args[1]);
                int id = Integer.parseInt(args[2]);
                int license = Integer.parseInt(args[3]);
                if((license>3)|(license<0)){
                    System.out.println("Error: Ilegal License number");return;
                }
                if( (pos!=3) | (addRemove==1) ){
                    System.out.println("Error: to many arguments provided");
                    return;
                }
                Result<Boolean> res = handleAddRemove(addRemove,pos,id);
                if (res.isError()) System.out.println(res.getMessage());
                else {
                    Result<Boolean> res2 = updateEmployeeLicense(id,license);
                    System.out.println("successfully executed");
                }
            }
        }
        catch (Exception e){
            System.out.println("Syntax Error");
        }
    }

    private Result<Boolean> updateEmployeeLicense(int id, int license) {
        String sLicense="";
        if(license==0) sLicense="B";
        if(license==1)sLicense="C";
        if(license==2) sLicense="C1";
        if(license==3)sLicense="CE";
        return serviceControl.updateEmployeeLicense(id,sLicense);
    }

    private Result<Boolean> handleAddRemove(int addRemove, int pos, int id) throws Exception {
        if((addRemove!=0 & addRemove!=1)|(pos<0 |pos>5))throw new Exception("Ilegal arguments");
        if(addRemove==0)return handleAddPosition(pos,id);
        else return handleRemovePosition(pos,id);
    }

    private Result<Boolean> handleAddPosition(int pos, int id) {

            if(pos==0) return serviceControl.addPositionToEmployee(id, Positions.cashier());
        if(pos==1) return serviceControl.addPositionToEmployee(id, Positions.stockKeeper());
        if(pos==2) return serviceControl.addPositionToEmployee(id, Positions.mover());
        if(pos==3) return  serviceControl.addPositionToEmployee(id, Positions.driver());
        if(pos==4) return  serviceControl.addPositionToEmployee(id, Positions.shiftManager());
        if(pos==5) return  serviceControl.addPositionToEmployee(id, Positions.hrManager());

        return doNothing();
    }

    private Result<Boolean> doNothing() {
        return new Result<Boolean>(true,"Ilegal Syntax");
    }

    private Result<Boolean> handleRemovePosition(int pos, int id) {

        if(pos==0) return serviceControl.removePositionToEmployee(id, Positions.cashier());
        if(pos==1) return serviceControl.removePositionToEmployee(id, Positions.stockKeeper());
        if(pos==2) return serviceControl.removePositionToEmployee(id, Positions.mover());
        if(pos==3) return serviceControl.removePositionToEmployee(id, Positions.driver());
        if(pos==4) return serviceControl.removePositionToEmployee(id, Positions.shiftManager());
        if(pos==5) return serviceControl.removePositionToEmployee(id, Positions.hrManager());
        return doNothing();
    }

    private void removeEmployee() {
        System.out.println("to remove an employee enter he/hers ID:");
        String cmd = scn.nextLine();
        try{
            int id = Integer.parseInt(cmd);
            Result<SEmployee> res = serviceControl.removeEmployee(id);
            if(res.isError()) System.out.println("Error occured: "+res.getMessage());
            else System.out.println("Employee "+res.getValue().toString()+" was successfully removed");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void addNewEmployee() {
        System.out.println("enter the properties of the new employee in the following manner:\n" +
                "<name>,<ID>,<salary>,<bank account number>,<start date>,<phone number>\n" +
                "example: gabi,206421745,56000,123456,22-03-2006,0501234567");
        String cmd = scn.nextLine();
        String[] args = cmd.split(",");
        if(args.length!=6) {System.out.println("Missing info");return;}
        try{
            String name = args[0];
            int id = Integer.parseInt(args[1]);
            int salary = Integer.parseInt(args[2]);
            int bankAcc = Integer.parseInt(args[3]);
            Date startDate = toDate(args[4]);
            String phoneNum = args[5];
            Result<SEmployee> res= serviceControl.addEmployee(name,id,salary,bankAcc,startDate,phoneNum);
            if(res.isError()) System.out.println(res.getMessage());
            else System.out.println("Successfully Added User:\n"+res.getValue());
        }
        catch (Exception e){
            System.out.println("Syntax error");
        }
    }

    private void printAllEmployees() {
        Result<List<SEmployee>> employees = serviceControl.getAllEmployees();
        if(employees.isError()) System.out.println(employees.getMessage());
        else{
            List<SEmployee> list = employees.getValue();
            System.out.println("Employees:");
            for(SEmployee s : list){
                System.out.println(s.toString());
            }
        }
    }

    public static Date toDate(String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date ret=null;
        try {
            ret = dateFormat.parse(date);
        } catch (ParseException ignored) {}
        return ret;
    }

    public void test(){
        System.out.println("######Employee Tests:");
        System.out.println("##Add employee test");
        Result<SEmployee> e1 = serviceControl.addEmployee("gabi",206421745,56000,123456,toDate("12-01-1999"),"1" );
        System.out.println("Success->"+e1.getValue());
        Result<SEmployee> e2 = serviceControl.addEmployee("marucs",206421745,56000,123456,toDate("12-01-1999"),"1");
        System.out.println("Should fail->"+e2.getMessage());
        System.out.println("##");
        System.out.println("##Add position test");
        Result<Boolean> b1 = serviceControl.addPositionToEmployee(206421745, Positions.cashier());
        Result<SEmployee> e3 = serviceControl.getEmployee(206421745);
        System.out.println("Should have cashier in positions\n"+e3.getValue());
        System.out.println("##remove position test");
        Result<Boolean> b2 = serviceControl.removePositionToEmployee(206421745, Positions.cashier());
        Result<SEmployee> e4 = serviceControl.getEmployee(206421745);
        System.out.println("should have empty positions\n"+e4.getValue());

    }
}
