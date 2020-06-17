package org.Jpa.enhance;


import org.Jpa.enhance.Jpa.JpaImpl;
import org.Jpa.enhance.aop.JpaSqlAspect;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;


public class EnhancePulg implements ApplicationContextInitializer {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        //注册到bean工厂
        DefaultListableBeanFactory configurableListableBeanFactory = (DefaultListableBeanFactory) applicationContext.getBeanFactory();


        GenericBeanDefinition JpaSqlAspectBeanDefinition  = new GenericBeanDefinition();
        JpaSqlAspectBeanDefinition.setBeanClassName("JpaSqlAspect");
        JpaSqlAspectBeanDefinition.setBeanClass(JpaSqlAspect.class);
        configurableListableBeanFactory.registerBeanDefinition("JpaSqlAspect",JpaSqlAspectBeanDefinition);


        GenericBeanDefinition JpaImplBeanDefinition  = new GenericBeanDefinition();
        JpaImplBeanDefinition.setBeanClassName("JpaImpl");
        JpaImplBeanDefinition.setBeanClass(JpaImpl.class);
        JpaImplBeanDefinition.setLazyInit(true);
        configurableListableBeanFactory.registerBeanDefinition("JpaImpl",JpaImplBeanDefinition);
    }
    public void  get(){
        System.out.println("dasdasd");
    }


}