package com.atguigu.gmall0624.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall0624.bean.SkuInfo;
import com.atguigu.gmall0624.bean.SpuImage;
import com.atguigu.gmall0624.bean.SpuSaleAttr;
import com.atguigu.gmall0624.service.ManageService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class SkuManageController {

    @Reference
    private ManageService manageService;

    // http://localhost:8082/spuImageList?spuId=60
    @RequestMapping("spuImageList")
    public List<SpuImage> getSpuImageList(SpuImage spuImage){
        return manageService.getSpuImageList(spuImage);
    }
    // http://localhost:8082/spuSaleAttrList?spuId=60
    @RequestMapping("spuSaleAttrList")
    public List<SpuSaleAttr> getSpuSaleAttrList(String spuId){

        return manageService.getSpuSaleAttrList(spuId);
    }
    // http://localhost:8082/saveSkuInfo
    // @RequestMapping post,get
    @RequestMapping("saveSkuInfo")
    public void saveSkuInfo(@RequestBody SkuInfo skuInfo){
        manageService.saveSkuInfo(skuInfo);
    }

}
