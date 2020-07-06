package gitee.hongzihao.ejpa;
import gitee.hongzihao.ejpa.Jpa.JpaImpl;
import gitee.hongzihao.ejpa.aop.JpaSqlAspect;
import gitee.hongzihao.ejpa.config.EjpConfig;
import gitee.hongzihao.ejpa.controller.JPAController;
import gitee.hongzihao.ejpa.module.entity.EjpaSlowQuery;
import gitee.hongzihao.ejpa.module.jpa.EjpaSlowQueryJpa;
import gitee.hongzihao.ejpa.run.EjpaRun;
import gitee.hongzihao.ejpa.service.impl.SlowQueryServiceImpl;
import gitee.hongzihao.ejpa.util.GenerateIdUtil;
import gitee.hongzihao.ejpa.util.SpringUtil;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;


public class EnhancePulg implements ApplicationContextInitializer {


    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        //注册到bean工厂
        DefaultListableBeanFactory configurableListableBeanFactory = (DefaultListableBeanFactory) applicationContext.getBeanFactory();


//        GenericBeanDefinition JpaSqlAspectBeanDefinition  = new GenericBeanDefinition();
//        JpaSqlAspectBeanDefinition.setBeanClassName("JpaSqlAspect");
//        JpaSqlAspectBeanDefinition.setBeanClass(JpaSqlAspect.class);
//        JpaSqlAspectBeanDefinition.setLazyInit(true);
//        configurableListableBeanFactory.registerBeanDefinition("JpaSqlAspect",JpaSqlAspectBeanDefinition);


//        GenericBeanDefinition JpaImplBeanDefinition  = new GenericBeanDefinition();
//        JpaImplBeanDefinition.setBeanClassName("JpaImpl");
//        JpaImplBeanDefinition.setBeanClass(JpaImpl.class);
//        JpaImplBeanDefinition.setLazyInit(true);
//        configurableListableBeanFactory.registerBeanDefinition("JpaImpl",JpaImplBeanDefinition);





        //////////////////////////////////////jpa

//        GenericBeanDefinition ejpaSlowQueryJpaBeanDefinition  = new GenericBeanDefinition();
//        ejpaSlowQueryJpaBeanDefinition.setBeanClassName("EjpaSlowQueryJpa");
//        ejpaSlowQueryJpaBeanDefinition.setBeanClass(EjpaSlowQueryJpa.class);
//        configurableListableBeanFactory.registerBeanDefinition("EjpaSlowQueryJpa",ejpaSlowQueryJpaBeanDefinition);




//        GenericBeanDefinition ejpaSlowQueryBeanDefinition  = new GenericBeanDefinition();
//        ejpaSlowQueryBeanDefinition.setBeanClassName("SlowQueryServiceImpl");
//        ejpaSlowQueryBeanDefinition.setBeanClass(SlowQueryServiceImpl.class);
//        ejpaSlowQueryBeanDefinition.setLazyInit(true);
//        configurableListableBeanFactory.registerBeanDefinition("SlowQueryServiceImpl",ejpaSlowQueryBeanDefinition);


//        GenericBeanDefinition  jPAControllerBeanDefinition  = new GenericBeanDefinition();
//        jPAControllerBeanDefinition.setBeanClassName("JPAController");
//        jPAControllerBeanDefinition.setBeanClass(JPAController.class);
//        jPAControllerBeanDefinition.setLazyInit(true);
//        configurableListableBeanFactory.registerBeanDefinition("JPAController",jPAControllerBeanDefinition);



//        GenericBeanDefinition  springUtilBeanDefinition  = new GenericBeanDefinition();
//        springUtilBeanDefinition.setBeanClassName("SpringUtil");
//        springUtilBeanDefinition.setBeanClass(SpringUtil.class);
//        configurableListableBeanFactory.registerBeanDefinition("SpringUtil",springUtilBeanDefinition);

//        GenericBeanDefinition ejpaRunBeanDefinition  = new GenericBeanDefinition();
//        ejpaRunBeanDefinition.setBeanClassName("EjpaRun");
//        ejpaRunBeanDefinition.setBeanClass(EjpaRun.class);
//        ejpaRunBeanDefinition.setLazyInit(true);
//        configurableListableBeanFactory.registerBeanDefinition("EjpaRun",ejpaRunBeanDefinition);


//        GenericBeanDefinition generateIdUtilDefinition  = new GenericBeanDefinition();
//        generateIdUtilDefinition.setBeanClassName("GenerateIdUtil");
//        generateIdUtilDefinition.setBeanClass(GenerateIdUtil.class);
//        generateIdUtilDefinition.setLazyInit(true);
//        configurableListableBeanFactory.registerBeanDefinition("GenerateIdUtil",generateIdUtilDefinition);


        GenericBeanDefinition ejpConfigDefinition  = new GenericBeanDefinition();
        ejpConfigDefinition.setBeanClassName("EjpConfig");
        ejpConfigDefinition.setBeanClass(EjpConfig.class);
        ejpConfigDefinition.setLazyInit(true);
        configurableListableBeanFactory.registerBeanDefinition("EjpConfig",ejpConfigDefinition);






    }


}