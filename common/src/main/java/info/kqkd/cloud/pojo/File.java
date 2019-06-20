package info.kqkd.cloud.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

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
@TableName("t_file")
public class File implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件类型 (jpg | png | mp3 | mp4 | zip )
     */
    private String type;

    /**
     * 是否为文件夹
     */
    private Boolean isDir;

    /**
     * 文件hash
     */
    private String fileSha1;

    /**
     * 文件大小
     */
    private Integer fileSize;

    /**
     * 文件存储位置
     */
    private String fileAddr;

    /**
     * 缩略图
     */
    private String smUrl;

    /**
     * 文件创建时间
     */
    private Timestamp createDate;

    /**
     * 文件更新时间
     */
    private Timestamp updateDate;

    /**
     * 文件状态(可用/禁用/已删除等状态)
     */
    @TableLogic
    private Integer status;

    /**
     * 备用字段1
     */
    private Integer ext1;

    /**
     * 备用字段2
     */
    private String ext2;

    @TableField(exist =  false)
    private List<UserFile> userFiles;


}
