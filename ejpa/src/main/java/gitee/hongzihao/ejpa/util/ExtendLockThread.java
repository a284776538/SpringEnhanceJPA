package gitee.hongzihao.ejpa.util;

import gitee.hongzihao.ejpa.service.MysqlService;

import javax.persistence.EntityManager;

public class ExtendLockThread extends  Thread {
    private  String id =null;
    private  int intTime ;
    private  MysqlService mysqlService =null;
    public  ExtendLockThread(String id , int intTime,MysqlService mysqlService ){
        this.id = id;
        this.mysqlService = mysqlService;
        this.intTime = intTime;
    }

    @Override
    public void run() {
        while (mysqlService.extendTimes(id,intTime)>0){
            try {
                System.out.println(intTime);
                Thread.sleep((intTime/2)*1000    );
            }catch (Exception e){

            }
        }
        System.out.println("finish lock");
    }
}
