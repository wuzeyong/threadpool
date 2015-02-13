package net.apusic.wzy.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Test1 {
    
     static class Info{
        private Date time;

        public Date getTime() {
            return time;
        }

        public void setTime(Date time) {
            this.time = time;
        }
        
        public void setTime(){
            this.time = new Date();
        }
        
    }
    
    public static void main(String[] args) {
        Date last = new Date();
        Calendar tmp = Calendar.getInstance();
        tmp.setTime(last);
        int valid = 2;
        tmp.add(Calendar.DAY_OF_YEAR, 2);
        System.out.println(tmp.getTime());
        
        Calendar login = Calendar.getInstance();
        login.set(2015, 0, 14, 14, 30);
        login.set(Calendar.SECOND, 50);
        System.out.println(login.getTime());
        System.out.println(tmp.getTime().before(login.getTime()));
      /*  Calendar calendar = Calendar.getInstance();
        calendar.set(tmp.get(Calendar.YEAR), tmp.get(Calendar.MONTH), tmp.get(Calendar.DATE),0,0,0);
        calendar.setTimeInMillis(calendar.getTimeInMillis()+23*60*60*1000+59*60*1000+59*1000+499);
        System.out.println(calendar.getTime());*/
    }
}
