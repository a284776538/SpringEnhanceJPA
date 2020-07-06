package gitee.hongzihao.ejpa.module.jpa;

import gitee.hongzihao.ejpa.annottation.ModelQuery;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public class DistributedLockJpa {

    @ModelQuery(value = "select count(id) from distributed_lock where keyword=:keyword "
            ,nativeQuery = true)
    public long countByKeyword(@Param("keyword") String keyword){return 0;};


    @ModelQuery(value = "select id  from distributed_lock where lock_time < :lockTime "
            ,retrunClass = "List<String>"
            ,nativeQuery = true)
    public Object findByLockTimeBefore(@Param("lockTime") Date lockTime){return 0;};

    @ModelQuery(value = "select id  from distributed_lock where lock_time < :lockTime and keyword =:keyword "
            ,retrunClass = "List<String>"
            ,nativeQuery = true)
    public Object findByLockTimeBeforeAndKeyword(@Param("keyword") String keyword,@Param("lockTime") Date lockTime){return 0;};
}
