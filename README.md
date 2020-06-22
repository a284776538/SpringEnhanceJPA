# SpringEnhanceJPA

#### 介绍
对Spring Data JPA 做的功能增强

#### 软件架构
目前实现功能，能使用Spirng data JPA对SQL,JPQL查询结果自动装配自定义结构


#### 安装教程

1.  下载jar包SpringEnhanceJPA /  0.0.1
2.  引入包

```
 compile group: 'cn.hutool', name: 'hutool-all', version:'4.1.15'
 compile group: 'commons-beanutils', name: 'commons-beanutils', version:'1.9.3'
```





#### 使用说明

1.  enhance-x.x.x.jar下载到本地引入项目
 **使用方法1** 
2. 创建ClassJpa查询文件
3.ModelQuery参数说明

- retrunClass = 添加返回值的类型，见下图findByVoteActivitieIdAndIsDeleteIsNull 第一个方法
- value = 查询语句，或者方法return的值2选一也是查询语句（如2个都写value 优先与return） 见3，4个方法
- countColumn = 分页指定count的字段，默认是*号，如sql select id from xxx 默认会自动使用 id。只用使用Page返回值才会执行count
- countQuery = 自己写count的sql与 countColumn 2选一，countQuery 优先与countColumn
- nativeQuery =是否是sql 或者 是jsql false 为jsql,true sql，默认false
- （注：使用方法的return可以自定义平拼接sql）
- （注：使用long，Long等基本类型不需要指定retrunClass见下图方法2）


![输入图片说明](https://images.gitee.com/uploads/images/2020/0622/160014_97be5fd8_341671.jpeg "Class查询.jpg")
 **使用方法2** 
1.创建interface查询文件
2.使用JPA可以不用设置retrunClass
3.没有注解ModelQuery的方法不会走定义对象查询
![输入图片说明](https://images.gitee.com/uploads/images/2020/0622/161140_d0dc5aa8_341671.png "屏幕截图.png")

