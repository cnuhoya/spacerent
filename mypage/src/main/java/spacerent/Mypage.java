package spacerent;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="Mypage_table")
public class Mypage {

        @Id
        @GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;
        private Long userid;
        private Long bookid;
        private String spacename;
        private String status;
        private Long spaceid;


        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
        public Long getUserid() {
            return userid;
        }

        public void setUserid(Long userid) {
            this.userid = userid;
        }
        public Long getBookid() {
            return bookid;
        }

        public void setBookid(Long bookid) {
            this.bookid = bookid;
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
        public Long getSpaceid() {
            return spaceid;
        }

        public void setSpaceid(Long spaceid) {
            this.spaceid = spaceid;
        }

}
