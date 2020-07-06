package gitee.hongzihao.ejpa.module.jpa;

import gitee.hongzihao.ejpa.annottation.ModelQuery;
import gitee.hongzihao.ejpa.module.entity.EjpaSlowQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 * 作品
 */
public class EjpaSlowQueryJpa {

    @ModelQuery(nativeQuery = true,retrunClass = "Page<gitee.hongzihao.ejpa.module.view.SlowSqlSynopsis>",
            countQuery = "select count(  method )  from ejpa_slow_query where (`sql` like  :keyword  or method like  :keyword   ) GROUP BY   method ",
            value = "select method,AVG(time) as time  from ejpa_slow_query where (`sql` like :keyword or method like :keyword   ) GROUP BY   method")
    public Object findEjpaSlowQueryByKeyword(@Param("keyword") String keyword, Pageable pageable){

        return null;};

    @ModelQuery(value = "select count(v.id)  from ejpa_slow_query v where v.vote_activitie_id = :voteActivitieId  and v.is_delete is null "
            ,nativeQuery = true)
    public long countByVoteActivitieIdAndIsDeleteIsNull(@Param("voteActivitieId") String VoteActivitieId){return 0;};


    @ModelQuery(nativeQuery = true,retrunClass = "List<gitee.hongzihao.ejpa.module.entity.EjpaSlowQuery>",
            value = "select *  from ejpa_slow_query where  method like :method  ")
    public Object findEjpaSlowQueryByMethod (@Param("method") String method){return null;};

    @ModelQuery(nativeQuery = true,retrunClass = "List<gitee.hongzihao.ejpa.module.entity.EjpaSlowQuery>",
            value = "select *  from ejpa_slow_query where  method = :method  ")
    public Object findEjpaSlowQueryByMethod (@Param("method") String method, Pageable pageable){return null;};


    @ModelQuery(nativeQuery = true,retrunClass = "List<String>",
            value = "select method  from ejpa_slow_query where  method like :method  GROUP BY   method ")
    public Object findEjpaSlowQueryLikeMethod (@Param("method") String method, Pageable pageable){return null;};

    @ModelQuery(nativeQuery = true,retrunClass = "List<String>",
            value = "select id  from ejpa_slow_query where  gmt_create <= :date  ")
    public Object findByBeforeDate (@Param("date") Date date){return null;};


}
