package gitee.hongzihao.ejpa.run;


//import com.plug.TestPulg;
//import org.Jpa.enhance.TestPulg;
import gitee.hongzihao.ejpa.service.MysqlService;
import gitee.hongzihao.ejpa.service.SlowQueryService;
import gitee.hongzihao.ejpa.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class EjpaRun  {

    private  static  ScheduledExecutorService publicScheduled =  new ScheduledThreadPoolExecutor(2);
    private static AtomicBoolean isRun =new AtomicBoolean(false);
    @Autowired
    @Lazy
    private SlowQueryService slowQueryService;

    @Autowired
    @Lazy
    private MysqlService mysqlService;

    @Value("${ejpa.open-lock}")
    private boolean openLock;

    private final Logger log = LoggerFactory.getLogger(getClass());

    public void run(String... args)  {
        if(isRun.get()!=false){
            return;
        }
        boolean isrun  = isRun.compareAndSet(false,true);
        if(!isrun){
            return;
        }
        try {
            slowQueryService.initSql();
            deleteExpiredKey();
            deleteSlowLog();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    /**
     * 每5秒执行一次
     */
    private void deleteExpiredKey(){
        if(!openLock){
            mysqlService.deleteExpiredKey();
            return;
        }
        publicScheduled.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    mysqlService.deleteExpiredKey();

                    System.out.println("deleteExpiredKey");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        },1,1,TimeUnit.SECONDS);

    }

    /**
     * 每10个小时执行一次清除
     */
    private void deleteSlowLog(){
        publicScheduled.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    slowQueryService.delete();
                    System.out.println("scheduleWithFixedDelay");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        },1,36000,TimeUnit.SECONDS);

    }
}


