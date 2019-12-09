package com.atguigu.gmall0624.manage.mapper;

import com.atguigu.gmall0624.bean.BaseAttrInfo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

// 通用mapper 的短板！ 不支持多表管理查询！
public interface BaseAttrInfoMapper extends Mapper<BaseAttrInfo> {
    // 根据三级分类Id 查询数据
    List<BaseAttrInfo> selectBaseAttrInfoListByCatalog3Id(String catalog3Id);

}
