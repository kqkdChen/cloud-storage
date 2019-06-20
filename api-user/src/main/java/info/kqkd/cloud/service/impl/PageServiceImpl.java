package info.kqkd.cloud.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import info.kqkd.cloud.dao.PageMapper;
import info.kqkd.cloud.pojo.Page;
import info.kqkd.cloud.service.IPageService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 可圈可丶
 * @since 2019-06-17
 */
@Service
public class PageServiceImpl extends ServiceImpl<PageMapper, Page> implements IPageService {

}
