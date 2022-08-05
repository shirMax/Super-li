package Backend.BusinessLayer.CallBacks;

import Backend.BusinessLayer.Employees.Shift;

import java.util.Collection;

public interface GetShiftsCallBack {

    public Collection<Shift> call() throws Exception;
}
