package Backend.BusinessLayer.CallBacks;

import java.util.Date;

public interface SwitchDriversCallBack {

        public boolean call(Date date, int oldDriver, int newDriver);
    }
