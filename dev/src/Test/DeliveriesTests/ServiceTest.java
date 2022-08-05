package DeliveriesTests;

import Backend.BusinessLayer.Employees.Employee;
import Backend.BusinessLayer.Employees.Positions;
import Backend.BusinessLayer.Employees.Shift;
import Backend.BusinessLayer.Employees.ShiftType;
import Backend.ServiceLayer.ObjectsEmployees.Result;
import Backend.ServiceLayer.ObjectsEmployees.SEmployee;
import Backend.ServiceLayer.ObjectsEmployees.SShift;
import Backend.ServiceLayer.Service;
import org.junit.jupiter.api.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTest {
    Service serCtrl;
    Employee employee1;
    SEmployee sEmployee;
    int indexId=2;
    String branch  = "beer sheba";
    @BeforeEach
    void setUp() {
        try{
            serCtrl = new Service();
            serCtrl.initial();

            serCtrl.addEmployee("employee1",indexId,1000,1234,new Date(2022, Calendar.APRIL,22),"1");
            Result<SEmployee> res = serCtrl.getEmployee(indexId);
            sEmployee = res.getValue();
            employee1 = res.getValue().getEmployee();
        }catch (Exception e){
            fail("Fail to build objects-Msg: "+e.getMessage());
        }

    }

    @AfterEach
    void tearDown() {
        indexId++;
    }

    @Test
    void addEmployee() {
        SEmployee e1=serCtrl.addEmployee("e1",1,2000,12345,new Date(2022, Calendar.APRIL,22),"1").getValue();
        Result<SEmployee> tempRes=serCtrl.getEmployee(e1.getId());
        if (!tempRes.isError()){
            SEmployee tempEmp=tempRes.getValue();
            assertAll(
                    ()->assertEquals(tempEmp.getName(),e1.getName()),
                    ()->assertEquals(tempEmp.getId(),e1.getId()),
                    ()->assertEquals(tempEmp.getSalary(),e1.getSalary()),
                    ()->assertEquals(tempEmp.getBankAccount(),e1.getBankAccount()),
                    ()->assertEquals(tempEmp.getStartDate(),e1.getStartDate())
            );
        }
        else fail("Error: "+tempRes.getMessage());
    }

    @Test
    void removeEmployee() {
        serCtrl.addEmployee("test1",404,404,404,new Date(1999,1,2),"1");
        Result<SEmployee> result = serCtrl.removeEmployee(404);
        assertFalse(result.isError(),"fail to remove");
    }

    @Test
    void addAndRemoveConstraint() {
//        Result<Boolean> addRes=serCtrl.addConstraint(1, ShiftType.evening(),employee1.getId());
//        if (!addRes.isError()){
//            Result <Boolean> removeResult= serCtrl.removeConstraint(1,ShiftType.evening(),employee1.getId());
//            if (removeResult.isError()) fail("Fail to Remove"+removeResult.getMessage());
//            else assertTrue(removeResult.getValue(),"remove result is FALSE");
//        }
//        else {
//            fail("Fail to ADD"+addRes.getMessage());
//        }
        Result<SEmployee> res1 =serCtrl.addEmployee("test1",404,404,404,new Date(1999,1,2),"1");
        Result<Boolean> addRes=serCtrl.addConstraint(1, ShiftType.evening(),404);
        if (!addRes.isError()){
            Result <Boolean> removeResult= serCtrl.removeConstraint(1,ShiftType.evening(),404);
            if (removeResult.isError()) {
                serCtrl.removeEmployee(404);
                fail("Fail to Remove"+removeResult.getMessage());
            }

            else {
                serCtrl.removeEmployee(404);
                assertTrue(removeResult.getValue(),"remove result is FALSE");
            }
        }

    }

    /**
     * preAssumption:addEmployee,addShift,addConstraint work properly.
     */
    @Test
    void addEmployeeToShift() {
        Date date=new Date(2023,Calendar.APRIL,1);
        String shiftType=ShiftType.evening();
        serCtrl.addShift(date,shiftType,branch);

        serCtrl.addConstraint(date.getDay(),shiftType,employee1.getId());
        Result<Boolean> addEmpToSftRes =serCtrl.addEmployeeToShift(employee1.getId(),date,shiftType,branch);

        Shift s = serCtrl.getShift(date,shiftType,branch).getValue().getShift();
        assertTrue(s.hasEmployee(employee1.getId()));




    }

    /**
     * preAssumption:getShift work properly and SShift.isEqual() is deeply compare.
     *
     */
    @Test
    void addShift() {
        Date date=new Date(2023,Calendar.APRIL,25);
        String shiftType= ShiftType.evening();
        Result<SShift> addShiftRes=serCtrl.addShift(date,shiftType,branch);

        if (addShiftRes.isError())fail(addShiftRes.getMessage());
        else {
            assertAll(
                    ()->assertTrue(addShiftRes.getValue().getShift().getBranch().equals(branch),"The shift and the result shift are different"),
                    ()->assertTrue(addShiftRes.getValue().getShift().getDate().equals(date),"Dates are unequal"),
                    ()->assertTrue(addShiftRes.getValue().getShift().getType().equals(shiftType),"Shift types are unequal")
            );
        }

    }

//add new and different position
    @Test
    void addNewPositionToEmployee() {
        String newPos= Positions.driver();
        Result<Boolean> addPosRes=serCtrl.addPositionToEmployee(employee1.getId(), newPos);
        if (addPosRes.isError()) {}
        else {
            Result<SEmployee> getEmpRes= serCtrl.getEmployee(employee1.getId());
            if (getEmpRes.isError()) fail(getEmpRes.getMessage());
            else assertTrue(getEmpRes.getValue().getPositions().contains(newPos),"employee do not contain new position");
        }
    }
    @Test
    void addExistingPositionToEmployee() {
        String positions=Positions.cashier();
        serCtrl.addPositionToEmployee(employee1.getId(), positions);
        Result<Boolean> addPosRes=serCtrl.addPositionToEmployee(employee1.getId(), positions);
        assertTrue(addPosRes.isError(), "cant add position that all ready existing,Error msg:= "+addPosRes.getMessage());

    }

    @Test
    void removePositionToEmployee() {
        serCtrl.removeEmployee(404);
        serCtrl.addEmployee("test1",404,404,404,new Date(1999,1,2),"1");
        String newPos=Positions.cashier();
        Result<Boolean> addPosRes=serCtrl.addPositionToEmployee(404, newPos);
        if (addPosRes.isError()) fail(addPosRes.getMessage());
        else {
            Result<SEmployee> getEmpRes= serCtrl.getEmployee(404);
            if (getEmpRes.isError()) fail(getEmpRes.getMessage());
            else {
                Result<Boolean> removeRes=serCtrl.removePositionToEmployee(404,newPos);
                if (removeRes.isError())fail(removeRes.getMessage());
                else assertFalse(getEmpRes.getValue().getPositions().contains(newPos),"employee still contain the position");
            }
        }
    }

    @Test
    void updateEmployeeSalary() {
        int newSalary=3000;
        Result<Boolean> updateResult=serCtrl.UpdateEmployeeSalary(employee1.getId(),newSalary);
        if (updateResult.isError()) fail(updateResult.getMessage());
        else {
            assertAll(()->assertTrue(updateResult.getValue(),"Update result is False")
                    ,()->assertEquals(serCtrl.getEmployee(employee1.getId()).getValue().getSalary(),newSalary,"new salary and current are not equals"));
        }
    }

    @Test
    void updateEmployeeBankAccount() {
        int newBank=12;
        Result<Boolean> updateResult=serCtrl.UpdateEmployeeBankAccount(employee1.getId(),newBank);
        if (updateResult.isError()) fail(updateResult.getMessage());
        else {
            assertAll(()->assertTrue(updateResult.getValue(),"Update result is False")
                    ,()->assertEquals(serCtrl.getEmployee(employee1.getId()).getValue().getBankAccount(),newBank,"new bank account and current are not equals"));
        }
    }

    @Test
    void updateEmployeeName() {
        String newName="newName";
        Result<Boolean> updateResult=serCtrl.UpdateEmployeeName(employee1.getId(),newName);
        if (updateResult.isError()) fail(updateResult.getMessage());
        else {
            assertAll(()->assertTrue(updateResult.getValue(),"Update result is False")
                    ,()->assertEquals(serCtrl.getEmployee(employee1.getId()).getValue().getName(),newName,"new name and current are not equals"));
        }
    }
//integration tests
    @Test
    void getDriverAndChangeName() {
        Date date= new Date(2022, Calendar.APRIL,22);
        SEmployee e1=serCtrl.addEmployee("e1",indexId,2000,
                12345,date,"1").getValue();
        indexId++;
        String newName="newNAme";
        serCtrl.addPositionToEmployee(e1.getId(),Positions.driver());
        serCtrl.UpdateEmployeeName(e1.getId(),newName);
        Result<List<SEmployee>>resListSEmp= serCtrl.getEmployeesByPosition(Positions.driver());
        SEmployee SEmp=resListSEmp.getValue().get(0);
        assertEquals(SEmp.getName(),newName);



    }
    @Test
    void getEmployeesByDriverPosition() {
        Date date= new Date(2022, Calendar.APRIL,22);
        SEmployee e1=serCtrl.addEmployee("e1",indexId,2000,
                12345,date,"1").getValue();
        indexId++;
        serCtrl.addPositionToEmployee(e1.getId(),Positions.driver());
        Result<List<SEmployee>>resListSEmp= serCtrl.getEmployeesByPosition(Positions.driver());
        SEmployee SEmp=resListSEmp.getValue().get(0);

        assertTrue(SEmp.getPositions().contains(Positions.driver()));
    }

    @Test
    void getDriverAndChangeSalary() {
        Date date= new Date(2022, Calendar.APRIL,22);
        SEmployee e1=serCtrl.addEmployee("e1",indexId,2000,
                12345,date,
                "1").getValue();
        indexId++;
        int newSalary=1000;
        serCtrl.addPositionToEmployee(e1.getId(),Positions.driver());
        serCtrl.UpdateEmployeeSalary(e1.getId(),newSalary);
        Result<List<SEmployee>>resListSEmp= serCtrl.getEmployeesByPosition(Positions.driver());
        SEmployee SEmp=resListSEmp.getValue().get(0);
        assertEquals(SEmp.getSalary(),newSalary);
    }
    @Test
    void getDriverAndChangeBankAcc() {
        Date date= new Date(2022, Calendar.APRIL,22);
        SEmployee e1=serCtrl.addEmployee("e1",indexId,2000,
                12345,date,"1").getValue();
        indexId++;
        int newBankAcc=1000;
        serCtrl.addPositionToEmployee(e1.getId(),Positions.driver());
        serCtrl.UpdateEmployeeBankAccount(e1.getId(),newBankAcc);
        Result<List<SEmployee>>resListSEmp= serCtrl.getEmployeesByPosition(Positions.driver());
        SEmployee SEmp=resListSEmp.getValue().get(0);
        assertEquals(SEmp.getBankAccount(),newBankAcc);
    }



}
