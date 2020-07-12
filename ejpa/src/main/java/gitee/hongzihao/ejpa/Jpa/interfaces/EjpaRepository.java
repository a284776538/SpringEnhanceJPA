package gitee.hongzihao.ejpa.Jpa.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EjpaRepository<T, ID> {

    @Modifying
    @Query(value = "update xxx st set st.visit = st.visit+1 where st.xxx = :xxx ",nativeQuery = true)
    <S extends T> int update(Iterable<S> entities);

    @Modifying
    @Query(value = "update xxx st set st.visit = st.visit+1 where st.xxx = :xxx ",nativeQuery = true)
    <S extends T> int update(S entity);
}
