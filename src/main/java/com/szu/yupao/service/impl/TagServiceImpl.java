package com.szu.yupao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szu.yupao.mapper.TagMapper;
import com.szu.yupao.pojo.Tag;
import com.szu.yupao.service.TagService;
import org.springframework.stereotype.Service;

/**
* @author 许猪配僧
* @description 针对表【tag(标签)】的数据库操作Service实现
* @createDate 2024-07-26 16:40:55
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService {

}




