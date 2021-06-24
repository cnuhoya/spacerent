package spacerent;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Payment_table")
public class Payment {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long payid;
    private Long bookid;
    private Long userid;
    private String status;
    private String spacename;

    @PostPersist
    public void onPostPersist(){
        Approved approved = new Approved();
        BeanUtils.copyProperties(this, approved);
        System.out.println("\n\nCircuit braker 확인 spacename: braker: " + approved.getSpacename() + "\n\n");      
        if(approved.getSpacename().equals("braker")){ 
            try{
              
                Thread.sleep(2500);
            }
            catch(Exception e){
                e.printStackTrace();
                System.out.println();
            }
        }
        System.out.println("\n\n$$$ GETSTATUS 확인 : " + getStatus() + "\n\n");
        System.out.println("\n\n$$$ APPROVE STATUS 확인 : " + approved.getStatus() + "\n\n");        
        if(getStatus().equals("cancel-pay")) {            
            approved.setStatus("cancel-pay");            
        }
        
        approved.publishAfterCommit();
    }


    public Long getPayid() {
        return payid;
    }

    public void setPayid(Long payid) {
        this.payid = payid;
    }
    public Long getBookid() {
        return bookid;
    }

    public void setBookid(Long bookid) {
        this.bookid = bookid;
    }
    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getSpacename() {
        return spacename;
    }

    public void setSpacename(String spacename) {
        this.spacename = spacename;
    }




}
