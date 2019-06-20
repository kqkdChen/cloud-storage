package info.kqkd.cloud.pojo;

import lombok.Data;

import java.util.List;

@Data
public class UserMulti {

    private String userAccount;

    private String rName;

    private String mName;

    private String mUrl;

    private String mIcon;

    private List<Page> pageList;

//    private String name;
//
//    private String url;
//
//    private String icon;

    public UserMulti() {
    }


    public UserMulti(String userAccount, String rName, String mName, String mUrl, String mIcon) {
        this.userAccount = userAccount;
        this.rName = rName;
        this.mName = mName;
        this.mUrl = mUrl;
        this.mIcon = mIcon;
    }
}
