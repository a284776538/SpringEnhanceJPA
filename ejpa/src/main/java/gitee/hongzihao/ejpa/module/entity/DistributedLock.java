package gitee.hongzihao.ejpa.module.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.context.annotation.Scope;

import javax.persistence.*;
import java.util.Date;

/**
 *分布式锁
 */
@Entity
@Table(name = "distributed_lock" )
@Scope(value = "prototype")
public class DistributedLock {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;

    @Column(name = "gmt_create")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date gmtCreate;

    @Column(name = "gmt_modified")
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date gmtModified;


    @Column(name = "lock_time")
    private Date lockTime;

    @Column(name = "keyword")
    private String  keyword;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Date getLockTime() {
        return lockTime;
    }

    public void setLockTime(Date lockTime) {
        this.lockTime = lockTime;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
