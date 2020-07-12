package gitee.hongzihao.ejpa.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.URLUtil;
import gitee.hongzihao.ejpa.module.entity.EjpaSlowQuery;
import gitee.hongzihao.ejpa.module.jpa.EjpaSlowQueryJpa;
import gitee.hongzihao.ejpa.service.SlowQueryService;
import gitee.hongzihao.ejpa.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class SlowQueryServiceImpl implements SlowQueryService {
    @PersistenceContext
    @Lazy
    protected EntityManager manager;
    @Value("${ejpa.log-slow-queries}")
    private boolean isLogSlow;

    @Value("${ejpa.slow-log-days}")
    private int days;

    @Value("${ejpa.log-slow-seconds}")
    private int seconds;

    @Autowired
    @Lazy
    protected EjpaSlowQueryJpa ejpaSlowQueryJpa;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(String method, String sql, long time) {
        seconds=seconds==0?3:seconds;
        if(isLogSlow!=true||sql==null||sql.equals("")||sql.indexOf("ejpa_slow_query")>=0||seconds>time){
            return;
        }
        String insertSql =  "INSERT INTO ejpa_slow_query (id, gmt_create, gmt_modified, is_delete,  `sql`,time,`method`)  " +
                " VALUES ('"+UUID.randomUUID().toString()+ RandomUtil.randomNumbers(3)+"'," +
                "now(),now(),null,'"+ URLUtil.encode(sql) +"',"+time+",'"+method+"')";
        manager.createNativeQuery(insertSql).executeUpdate();
    }

    @Override
    public Page<EjpaSlowQuery> get(String keyword, int time) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete() {
        days=days==0?5:days;
       List<String> ids = (List<String>) ejpaSlowQueryJpa.findByBeforeDate(DateUtil.offsetDay(new Date(),days*-1));

//        List<String> ids = (List<String>) ejpaSlowQueryJpa.findByBeforeDate(null);
       String idstring="";
       if(ids!=null&&ids.size()>0){
           for (String id :ids){
               idstring =idstring+"'"+id+"',";
           }
           String delete ="DELETE FROM ejpa_slow_query WHERE id in ("+idstring.substring(0,idstring.length()-1)+")";
//           SpringUtil.getBean(EntityManager.class).createNativeQuery(delete).executeUpdate();
           manager.createNativeQuery(delete).executeUpdate();
       }


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initSql() {
        String sql = "CREATE TABLE `ejpa_slow_query` ( " +
                "  `id` varchar(255) NOT NULL, " +
                "  `gmt_create` datetime DEFAULT NULL, " +
                "  `gmt_modified` datetime DEFAULT NULL, " +
                "  `is_delete` datetime DEFAULT NULL, " +
                "  `time` int(11) NOT NULL, " +
                "  `sql` varchar(255) NOT NULL, " +
                "  `method` varchar(255) DEFAULT NULL, " +
                "  PRIMARY KEY (`id`), " +
                "  UNIQUE KEY `key_id` (`id`) USING BTREE, " +
                "  KEY `key_method` (`method`) USING BTREE, " +
                "  KEY `key_time` (`time`) USING BTREE, " +
                "  KEY `key_sql` (`sql`,`method`) USING BTREE " +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
        try {
            manager.createNativeQuery(sql).executeUpdate();
        } catch (Exception e) {

        }
        sql = "CREATE TABLE `distributed_lock` (  " +
                "  `id` varchar(100) CHARACTER SET utf8 NOT NULL,  " +
                "  `keyword` varchar(255) CHARACTER SET utf8 DEFAULT NULL,  " +
                "  `lock_time` datetime DEFAULT NULL,  " +
                "  `gmt_create` datetime DEFAULT NULL,  " +
                "  `gmt_modified` datetime DEFAULT NULL,  " +
                "  `is_delete` tinyint(1) DEFAULT '0',  " +
                "  PRIMARY KEY (`id`),  " +
                "  UNIQUE KEY `key_id` (`id`) USING BTREE,  " +
                "  UNIQUE KEY `key_unuque` (`keyword`) USING BTREE,  " +
                "  KEY `key_lock_time` (`lock_time`) USING BTREE  " +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
        try {
            manager.createNativeQuery(sql).executeUpdate();
        } catch (Exception e) {

        }

    }


}
