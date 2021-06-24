package spacerent;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Book_table")
public class Book {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long bookid;
    private Long userid;
    private String status;
    private String spacename;

    @PostPersist
    public void onPostPersist(){
        Booked booked = new Booked();
        BeanUtils.copyProperties(this, booked);
        booked.publishAfterCommit();

        spacerent.external.Payment payment = new spacerent.external.Payment();
        payment.setBookid(booked.getBookid());
        payment.setSpacename(booked.getSpacename());
        payment.setStatus("booking");
        payment.setUserid(booked.getBookid());
        BookApplication.applicationContext.getBean(spacerent.external.PaymentService.class).pay(payment);


    }

    @PostUpdate
    public void onPostUpdate(){
         
        System.out.println("\n\n##### app onPostUpdate, getStatus() : " + getStatus() + "\n\n");
        if(getStatus().equals("cancel-booking")) {
            Bookcancelled bookcancelled = new Bookcancelled();
            BeanUtils.copyProperties(this, bookcancelled);
            bookcancelled.publishAfterCommit();
            
            spacerent.external.Payment payment = new spacerent.external.Payment();
            payment.setBookid(bookcancelled.getBookid());
            payment.setSpacename(bookcancelled.getSpacename());
            payment.setStatus("cancel-booking");
            payment.setUserid(bookcancelled.getBookid());
            BookApplication.applicationContext.getBean(spacerent.external.PaymentService.class).pay(payment);            
        }        


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

    public class findByBookId {
    }




}
