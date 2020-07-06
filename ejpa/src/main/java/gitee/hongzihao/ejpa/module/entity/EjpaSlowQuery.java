package gitee.hongzihao.ejpa.module.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import gitee.hongzihao.ejpa.annottation.GeneratedId;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "ejpa_slow_query" )
public class EjpaSlowQuery  implements Serializable {


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

  @Column(name = "is_delete")
  @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
  private Date isDelete = null;


  private String sql;

  private  int time;

  private String method;


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

  public Date getIsDelete() {
    return isDelete;
  }

  public void setIsDelete(Date isDelete) {
    this.isDelete = isDelete;
  }

  public String getSql() {
    return sql;
  }

  public void setSql(String sql) {
    this.sql = sql;
  }

  public int getTime() {
    return time;
  }

  public void setTime(int time) {
    this.time = time;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }
}
