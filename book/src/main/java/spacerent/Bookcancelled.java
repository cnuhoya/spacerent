
package spacerent;

public class Bookcancelled extends AbstractEvent {

    private Long bookid;
    private Long userid;
    private String status;
    private String spacename;

    public Long getId() {
        return bookid;
    }

    public void setId(Long bookid) {
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

