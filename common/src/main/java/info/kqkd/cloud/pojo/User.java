package info.kqkd.cloud.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author 可圈可丶
 * @since 2019-06-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    private String userAccount;

    private String userPassword;

    private String userEmail;

    private String userPhone;

    private Integer roleId;

    @TableField(exist = false)
    private Role role;

    /* 逻辑删除字段 */
    @TableLogic
    private Integer status;

    private Timestamp createDate;

    private Timestamp updateDate;

    private Integer ext1;

    private String ext2;

    @TableField(exist =  false)
    private List<UserFile> userFiles;

    @TableField(exist = false)
    private String loginIp;


}
