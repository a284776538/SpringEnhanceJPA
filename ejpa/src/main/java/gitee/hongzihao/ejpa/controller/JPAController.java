package gitee.hongzihao.ejpa.controller;



import cn.hutool.core.util.URLUtil;
import gitee.hongzihao.ejpa.module.entity.EjpaSlowQuery;
import gitee.hongzihao.ejpa.module.jpa.EjpaSlowQueryJpa;
import gitee.hongzihao.ejpa.module.view.SlowSqlSynopsis;
import gitee.hongzihao.ejpa.service.SlowQueryService;
import gitee.hongzihao.ejpa.service.impl.SlowQueryServiceImpl;
import gitee.hongzihao.ejpa.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;


/**
 * 公共的接口
 */
@RestController
@RequestMapping("/ejpa")
@Order(12)
//@DefaultProperties(defaultFallback = "defaultFallback")
public class JPAController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    @Lazy
    private  EjpaSlowQueryJpa ejpaSlowQueryJpa ;

    @Autowired
    @Lazy
    private SlowQueryService slowQueryService;

    /**
     * http://localhost:12000/ejpa/getSlowSqlByMethod?method=find
     * 保存活动 - saveVoteActivitie
     * @param request
     * @return
     */
    @RequestMapping(value = "/getSlowSqlByMethod", method = {RequestMethod.GET ,RequestMethod.POST})
    public Msg getSlowSqlByMethod(HttpServletRequest request,String method,Pageable pageable) throws  Exception{
        ConcurrentMap<String,Object> returnMap = new ConcurrentHashMap();
                List<String> ejs = (List<String>) ejpaSlowQueryJpa.findEjpaSlowQueryLikeMethod(method==null?"%%":"%"+method+"%",pageable);
        if(ejs==null||ejs.size()==0){
            return Msg.MsgSuccess("200","查询成功", null);
        }
        Pageable pageable2  = new PageRequest(0, 10, Sort.Direction.DESC,"time");

        List< CompletableFuture<List<EjpaSlowQuery>> > futures = new ArrayList<>();

       ExecutorService publicScheduled = Executors.newFixedThreadPool(5);
        for (String key: ejs){
            CompletableFuture<List<EjpaSlowQuery>>  datas  = CompletableFuture.supplyAsync(() ->(List<EjpaSlowQuery>)ejpaSlowQueryJpa.findEjpaSlowQueryByMethod(key,pageable2),publicScheduled);
            returnMap.put(key,datas);
        }
        publicScheduled.shutdown();
        //等待所以线程退出
        publicScheduled.awaitTermination(1, TimeUnit.MINUTES);
        for (String key: ejs){
            CompletableFuture<List<EjpaSlowQuery>> future = (CompletableFuture<List<EjpaSlowQuery>>) returnMap.get(key);
            returnMap.put(key,future.get());
        }
        return Msg.MsgSuccess("200","查询成功",returnMap);
    }
    /**
     * http://localhost:12000/ejpa/getSlowSql?keyword=select
     * 保存活动 - saveVoteActivitie
     * @param request
     * @return
     */
    @RequestMapping(value = "/getSlowSql", method = {RequestMethod.GET ,RequestMethod.POST})
    public Msg getSlowSql(HttpServletRequest request,String keyword,Pageable pageable) throws  Exception{
        keyword =keyword==null?"%%":"%"+keyword+"%";
//        SlowQueryServiceImpl slowQueryServiceImpl = (SlowQueryServiceImpl) SpringUtil.getBean(SlowQueryService.class);
        Page<SlowSqlSynopsis> returnData = (Page<SlowSqlSynopsis>) ejpaSlowQueryJpa.findEjpaSlowQueryByKeyword(keyword,pageable);
        return Msg.MsgSuccess("200","查询成功",returnData);
    }


}
