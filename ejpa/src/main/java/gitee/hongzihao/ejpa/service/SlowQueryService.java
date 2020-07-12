package gitee.hongzihao.ejpa.service;

import gitee.hongzihao.ejpa.module.entity.EjpaSlowQuery;
import org.springframework.data.domain.Page;

public interface SlowQueryService {

    void  save(String method ,String sql,long time);

    Page<EjpaSlowQuery> get(String keyword, int time);


    void  delete();

    void initSql();
}
