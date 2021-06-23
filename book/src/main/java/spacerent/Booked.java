package spacerent;

public class Booked extends AbstractEvent {

    private Long id;
    private Long userid;
    private String status;
    private String spacename;

    public Booked(){
        super();
    }

    public Long getBookid() {
        return id;
    }

    public void setBookid(Long id) {
        this.id = id;
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
