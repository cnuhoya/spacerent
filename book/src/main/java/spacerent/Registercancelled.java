
package spacerent;

public class Registercancelled extends AbstractEvent {

    private Long spaceid;
    private Long userid;
    private String spacename;
    private String status;
    private Long bookid;

    public Long getId() {
        return spaceid;
    }

    public void setId(Long spaceid) {
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

