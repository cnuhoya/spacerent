package spacerent;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Space_table")
public class Space {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long spaceid;
    private Long userid;
    private String spacename;
    private String status;
    private Long bookid;

    @PostPersist
    public void onPostPersist(){
        Registered registered = new Registered();
        BeanUtils.copyProperties(this, registered);
        registered.publishAfterCommit();


    }

    @PostUpdate
    public void onPostUpdate(){
        Registercancelled registercancelled = new Registercancelled();
        BeanUtils.copyProperties(this, registercancelled);
        registercancelled.publishAfterCommit();


    }


    public Long getSpaceid() {
        return spaceid;
    }

    public void setSpaceid(Long spaceid) {
        this.spaceid = spaceid;
    }
    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }
    public String getSpacename() {
        return spacename;
    }

    public void setSpacename(String spacename) {
        this.spacename = spacename;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public Long getBookid() {
        return bookid;
    }

    public void setBookid(Long bookid) {
        this.bookid = bookid;
    }




}
