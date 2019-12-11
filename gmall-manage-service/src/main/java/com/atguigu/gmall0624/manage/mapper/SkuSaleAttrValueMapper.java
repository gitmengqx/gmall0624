package com.atguigu.gmall0624.manage.mapper;

import com.atguigu.gmall0624.bean.SkuSaleAttrValue;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SkuSaleAttrValueMapper extends Mapper<SkuSaleAttrValue> {
    /**
     * 通过spuId 查询sku与销售属性中间表集合
     * @param spuId
     * @return
     */
    List<SkuSaleAttrValue> selectSkuSaleAttrValueListBySpu(String spuId);

    /**
     * 根据spuId 查询map 集合
     * @param spuId
     * @return
     */
    List<Map> getSaleAttrValuesBySpu(String spuId);
}
