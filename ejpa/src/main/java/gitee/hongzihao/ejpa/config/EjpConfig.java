package gitee.hongzihao.ejpa.config;


import gitee.hongzihao.ejpa.Jpa.JpaImpl;
import gitee.hongzihao.ejpa.aop.JpaSaveAspect;
import gitee.hongzihao.ejpa.aop.JpaSqlAspect;
import gitee.hongzihao.ejpa.aop.JpaUpdateAspect;
import gitee.hongzihao.ejpa.controller.JPAController;
import gitee.hongzihao.ejpa.module.jpa.DistributedLockJpa;
import gitee.hongzihao.ejpa.module.jpa.EjpaSlowQueryJpa;
import gitee.hongzihao.ejpa.run.EjpaRun;
import gitee.hongzihao.ejpa.service.MysqlService;
import gitee.hongzihao.ejpa.service.SlowQueryService;
import gitee.hongzihao.ejpa.service.impl.MysqlServiceImpl;
import gitee.hongzihao.ejpa.service.impl.SlowQueryServiceImpl;
import gitee.hongzihao.ejpa.util.GenerateIdUtil;
import gitee.hongzihao.ejpa.util.SpringUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class EjpConfig {


    @Bean
    @Lazy
    public SlowQueryService getSlowQueryService(){
            return  new SlowQueryServiceImpl();
    }


    @Bean
    @Lazy
    public EjpaRun getEjpaRun(){
        return  new EjpaRun();
    }

    @Bean
    @Lazy
    public GenerateIdUtil getGenerateIdUtil(){
        return  new GenerateIdUtil();
    }


    @Bean
    @Lazy
    public JpaImpl getJpaImpl(){
        return  new JpaImpl();
    }

    @Bean
    @Lazy
    public EjpaSlowQueryJpa getEjpaSlowQueryJpa(){
        return  new EjpaSlowQueryJpa();
    }

    @Bean
    @Lazy
    public SpringUtil getSpringUtil(){
        return  new SpringUtil();
    }

    @Bean
    @Lazy
    public JpaSqlAspect getJpaSqlAspect(){
        return  new JpaSqlAspect();
    }

    @Bean
    @Lazy
    public JpaSaveAspect getJpaSaveAspect(){
        return  new JpaSaveAspect();
    }

    @Bean
    @Lazy
    public JPAController getJPAController(){ return  new JPAController(); }


    @Bean
    @Lazy
    public DistributedLockJpa getDistributedLockJpa(){ return  new DistributedLockJpa(); }

    @Bean
    @Lazy
    public MysqlService getMysqlService(){ return  new MysqlServiceImpl(); }

    @Bean
    @Lazy
    public JpaUpdateAspect getJpaUpdateAspect(){ return  new JpaUpdateAspect(); }



}
