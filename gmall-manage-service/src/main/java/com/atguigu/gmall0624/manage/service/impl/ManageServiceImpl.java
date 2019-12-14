package com.atguigu.gmall0624.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall0624.bean.*;
import com.atguigu.gmall0624.config.RedistUtil;
import com.atguigu.gmall0624.constant.ManageConst;
import com.atguigu.gmall0624.manage.mapper.*;
import com.atguigu.gmall0624.service.ManageService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ManageServiceImpl implements ManageService {
    // 调用mapper层
    @Autowired
    private BaseCatalog1Mapper baseCatalog1Mapper;

    @Autowired
    private BaseCatalog2Mapper baseCatalog2Mapper;

    @Autowired
    private BaseCatalog3Mapper baseCatalog3Mapper;

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;

    @Autowired
    private SpuInfoMapper spuInfoMapper;

    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;

    @Autowired
    private SpuImageMapper spuImageMapper;

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private SkuImageMapper skuImageMapper;

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    private RedistUtil redistUtil;


    @Override
    public List<BaseCatalog1> getCatalog1() {
        return baseCatalog1Mapper.selectAll();
    }

    @Override
    public List<BaseCatalog2> getCatalog2(String catalog1Id) {
//        return baseCatalog2Mapper.selectByExample();
        // select * from basecatalog2 from where catalog1Id= ？
        BaseCatalog2 baseCatalog2 = new BaseCatalog2();
        baseCatalog2.setCatalog1Id(catalog1Id);
        return baseCatalog2Mapper.select(baseCatalog2);
    }

    @Override
    public List<BaseCatalog2> getCatalog2(BaseCatalog2 baseCatalog2) {
        return baseCatalog2Mapper.select(baseCatalog2);
    }

    @Override
    public List<BaseCatalog3> getCatalog3(BaseCatalog3 baseCatalog3) {
        return baseCatalog3Mapper.select(baseCatalog3);
    }

    @Override
    public List<BaseAttrInfo> getAttrInfoList(BaseAttrInfo baseAttrInfo) {
        // select * from baseAttrInfo where catalog3Id = ?
        return baseAttrInfoMapper.select(baseAttrInfo);


    }

    @Override
    @Transactional
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {

        // 修改！ baseAttrInfo
        if (baseAttrInfo.getId() != null && baseAttrInfo.getId().length() > 0) {
            baseAttrInfoMapper.updateByPrimaryKeySelective(baseAttrInfo);
        } else {
            // 保存！
            // baseAttrInfo 代表页面传递过来的数据！
            // 分别插入到两张表 baseAttrInfo ，baseAttrValue
            // attrName , catalog3Id
            // System.out.println("插入之前："+baseAttrInfo.getId());
            baseAttrInfoMapper.insertSelective(baseAttrInfo);
        }
        System.out.println("插入之后：" + baseAttrInfo.getId());
        //  baseAttrValue  修改 {先删除原有数据，在新增所有的数据！}
        // delete * from baseAttrValue where attrId = ? baseAttrInfo.getId()
        BaseAttrValue baseAttrValueDel = new BaseAttrValue();
        baseAttrValueDel.setAttrId(baseAttrInfo.getId());
        baseAttrValueMapper.delete(baseAttrValueDel);
        System.out.println("删除数据");

        // baseAttrValue |  接收baseAttrValue 的集合
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        if (attrValueList != null && attrValueList.size() > 0) {
            for (BaseAttrValue baseAttrValue : attrValueList) {
                // 保存数据  valueName , attrId  = baseAttrInfo.getId();
                baseAttrValue.setAttrId(baseAttrInfo.getId()); // baseAttrInfo.getId();主键
                baseAttrValueMapper.insertSelective(baseAttrValue);
            }
        }
    }

    @Override
    public List<BaseAttrValue> getAttrValueList(String attrId) {
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(attrId);
        return baseAttrValueMapper.select(baseAttrValue);
    }

    @Override
    public BaseAttrInfo getAtrrInfo(String attrId) {
        // select * from baseAttrInfo where id = attrId;
        BaseAttrInfo baseAttrInfo = baseAttrInfoMapper.selectByPrimaryKey(attrId);

        // 赋值
        baseAttrInfo.setAttrValueList(getAttrValueList(attrId));
        return baseAttrInfo;
    }

    @Override
    public List<SpuInfo> getSpuList(String catalog3Id) {
        return null;
    }

    @Override
    public List<SpuInfo> getSpuList(SpuInfo spuInfo) {
        return spuInfoMapper.select(spuInfo);
    }

    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return baseSaleAttrMapper.selectAll();
    }

    @Override
    @Transactional
    public void saveSpuInfo(SpuInfo spuInfo) {
        // spuInfo 表示从前台页面传递过来的数据
//        spuInfo
        spuInfoMapper.insertSelective(spuInfo);
//        spuImage'
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();

        if (spuImageList != null && spuImageList.size() > 0) {
            for (SpuImage spuImage : spuImageList) {
                spuImage.setSpuId(spuInfo.getId());
                spuImageMapper.insertSelective(spuImage);
            }
        }
//        spuSaleAttr
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if (spuSaleAttrList != null && spuSaleAttrList.size() > 0) {
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                spuSaleAttr.setSpuId(spuInfo.getId());
                spuSaleAttrMapper.insertSelective(spuSaleAttr);

                //        spuSaleAttrValue
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
//                if (spuSaleAttrValueList!=null && spuSaleAttrValueList.size()>0){
//                    for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
//                        spuSaleAttrValue.setSpuId(spuInfo.getId());
//                        spuSaleAttrValueMapper.insertSelective(spuSaleAttrValue);
//                    }
//                }
                if (checkListIsEmpty(spuSaleAttrValueList)) {
                    for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        spuSaleAttrValueMapper.insertSelective(spuSaleAttrValue);
                    }
                }
            }
        }
    }

    @Override
    public List<SpuImage> getSpuImageList(SpuImage spuImage) {
        return spuImageMapper.select(spuImage);
    }

    @Override
    public List<BaseAttrInfo> getAttrInfoList(String catalog3Id) {
        /*
        SELECT * FROM base_attr_info bai INNER JOIN base_attr_value bav ON bai.id = bav.attr_id WHERE bai.catalog3_id=61;
         */
        return baseAttrInfoMapper.selectBaseAttrInfoListByCatalog3Id(catalog3Id);

    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(String spuId) {

        return spuSaleAttrMapper.selectSpuSaleAttrList(spuId);
    }

    @Override
    @Transactional
    public void saveSkuInfo(SkuInfo skuInfo) {
//        skuInfo
        skuInfoMapper.insertSelective(skuInfo);
//        skuAttrValue
//        SkuAttrValue skuAttrValue = new SkuAttrValue();
//        skuAttrValue.setSkuId(skuInfo.getId());
//
//        skuAttrValueMapper.insertSelective(skuAttrValue);
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if (skuAttrValueList != null && skuAttrValueList.size() > 0) {
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuInfo.getId());
                skuAttrValueMapper.insertSelective(skuAttrValue);
            }
        }

//        skuSaleAttrValue
//        SkuSaleAttrValue skuSaleAttrValue = new SkuSaleAttrValue();
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if (checkListIsEmpty(skuSaleAttrValueList)) {
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                skuSaleAttrValue.setSkuId(skuInfo.getId());
                skuSaleAttrValueMapper.insertSelective(skuSaleAttrValue);
            }
        }
//         skuImage
//        SkuImage skuImage = new SkuImage();
        if (checkListIsEmpty(skuInfo.getSkuImageList())) {
            for (SkuImage skuImage : skuInfo.getSkuImageList()) {
                skuImage.setSkuId(skuInfo.getId());
                skuImageMapper.insertSelective(skuImage);
            }
        }
    }

    @Override
    public SkuInfo getSkuInfo(String skuId) {
        //. return getSkuInfoRedisSet(skuId);
        return getSkuInfoRedisson(skuId);
    }

    private SkuInfo getSkuInfoRedisson(String skuId) {
        SkuInfo skuInfo = new SkuInfo();
        Jedis jedis = null;
        try {
            // 获取jedis
            jedis = redistUtil.getJedis();
            // 定义key
            String skuKey = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKUKEY_SUFFIX;
            // 获取缓存的数据
            String skuJson = jedis.get(skuKey);
            // 什么时候上锁！
            if (skuJson == null) {
                // 获取数据库数据并放入缓存
                Config config = new Config();
                config.useSingleServer().setAddress("redis://192.168.67.225:6379");
                // 获取redisson
                RedissonClient redisson = Redisson.create(config);

                RLock lock = redisson.getLock("my-lock");

                // lock.lock(10,TimeUnit.SECONDS); // set k2 v2 px 10000 nx

                boolean res = lock.tryLock(100, 10, TimeUnit.SECONDS);
                if (res) {
                    try {
                        // 走数据库DB
                        skuInfo = getSkuInfoDB(skuId);
                        // 表示将商品详情永远的存在了缓存！
                        // jedis.set(skuKey, JSON.toJSONString(skuInfo));
                        // 设置过期时间
                        jedis.setex(skuKey, ManageConst.SKUKEY_TIMEOUT, JSON.toJSONString(skuInfo));
                    } finally {
                        lock.unlock();
                    }
                }
            } else {
                // 有缓存 获取缓存数据 skuJson 转换成对象
                skuInfo = JSON.parseObject(skuJson, SkuInfo.class);
                return skuInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 如何解决空指针问题！
            if (jedis != null) {
                // 关闭
                jedis.close();
            }
        }
        return getSkuInfoDB(skuId);
    }

    // redis--Set 命令做分布式锁！
    private SkuInfo getSkuInfoRedisSet(String skuId) {
        SkuInfo skuInfo = new SkuInfo();
        Jedis jedis = null;
        try {
            // 获取jedis
            jedis = redistUtil.getJedis();
            // 定义key
            String skuKey = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKUKEY_SUFFIX;
            // 获取缓存的数据
            String skuJson = jedis.get(skuKey);
            // 什么时候上锁！
            if (skuJson == null) {
                // 说明缓存中没有数据
                System.out.println("缓存中没有数据！");
                // 查询数据库 上锁！ set k2 v2 px 10000 nx | k2 是锁！锁的时间【10000】
                // 定义一个锁的key = sku:skuId:lock
                String skuLockKey = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKULOCK_SUFFIX;
                // 锁的值
                String token = UUID.randomUUID().toString().replace("-", "");
                // 执行锁！
                String lockKey = jedis.set(skuLockKey, token, "NX", "PX", ManageConst.SKULOCK_EXPIRE_PX);
                // 上锁成功！
                if ("OK".equals(lockKey)) {
                    System.out.println("获取到分布式锁！");
                    // 查询数据库并放入缓存！
                    skuInfo = getSkuInfoDB(skuId);
                    // 表示将商品详情永远的存在了缓存！
                    // jedis.set(skuKey, JSON.toJSONString(skuInfo));
                    // 设置过期时间
                    jedis.setex(skuKey, ManageConst.SKUKEY_TIMEOUT, JSON.toJSONString(skuInfo));

                    // 解锁  jedis.del(lockKey); 误删锁！ lua 脚本。。。
                    String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                    // 执行删除锁！
                    jedis.eval(script, Collections.singletonList(skuLockKey), Collections.singletonList(token));

                    return skuInfo;
                } else {
                    // 等待：
                    Thread.sleep(1000);

                    // 调用方法
                    return getSkuInfo(skuId);
                }
            } else {
                // 有缓存 获取缓存数据 skuJson 转换成对象
                skuInfo = JSON.parseObject(skuJson, SkuInfo.class);
                return skuInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 如何解决空指针问题！
            if (jedis != null) {
                // 关闭
                jedis.close();
            }
        }
        return getSkuInfoDB(skuId);
    }

    // 获取数据库中的数据！
    private SkuInfo getSkuInfoDB(String skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);
        // 给skuInfo.skuImageList 赋值
        // select * from skuImage where skuId =skuId
        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuId);
        List<SkuImage> skuImageList = skuImageMapper.select(skuImage);
        skuInfo.setSkuImageList(skuImageList);
        // 没有查询平台属性集合
        SkuAttrValue skuAttrValue = new SkuAttrValue();
        skuAttrValue.setSkuId(skuId);
        skuInfo.setSkuAttrValueList(skuAttrValueMapper.select(skuAttrValue));

        return skuInfo;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(SkuInfo skuInfo) {
        return spuSaleAttrMapper.selectSpuSaleAttrListCheckBySku(skuInfo.getId(), skuInfo.getSpuId());
    }

    @Override
    public List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId) {
        return skuSaleAttrValueMapper.selectSkuSaleAttrValueListBySpu(spuId);
    }

    @Override
    public Map getSkuValueIdsMap(String spuId) {
        HashMap<Object, Object> returnMap = new HashMap<>();

        // 给returnMap 赋值 skuValueIdsMap.put("122|126","37") skuValueIdsMap.put("123|126","38")
        /*
            调用mapper 执行当前的sql 语句
            SELECT group_concat(sale_attr_value_id ORDER BY sale_attr_id SEPARATOR '|') value_id,sku_id
            FROM sku_sale_attr_value ssav INNER  JOIN sku_info si ON  ssav.sku_id = si.id
            WHERE si.spu_id = 60
            GROUP BY sku_id;
         */
        List<Map> mapList = skuSaleAttrValueMapper.getSaleAttrValuesBySpu(spuId);
        if (mapList != null && mapList.size() > 0) {
            for (Map map : mapList) {
//                String value_id = (String) map.get("value_ids");//122|126
//                String sku_id = (String) map.get("sku_id"); // 37
                returnMap.put(map.get("value_ids"), map.get("sku_id"));
            }
        }
        return returnMap;
    }

    @Override
    public List<BaseAttrInfo> getAttrList(List<String> attrValueIdList) {
        /*
            SELECT * FROM base_attr_info bai INNER JOIN base_attr_value bav ON bai.id=bav.attr_id
                WHERE bav.id IN (171,81,120,167,82,83);
             两种方式：
                一种：attrValueIdList 变成字符串 171,81,120,167,82,83
                二种：使用mybatis 的动态sql <foreach> </foreach>
                <select id="selectPostIn" resultType="domain.blog.Post">
                  SELECT *
                  FROM POST P
                  WHERE ID in
                  <foreach item="item" index="index" collection="attrValueIdList"
                      open="(" separator="," close=")">
                        #{item}
                  </foreach>
                </select>
         */
        // attrValueIdList 将其转换为字符串
        String attrValueIds  = StringUtils.join(attrValueIdList.toArray(), ",");

        System.out.println(attrValueIds); // 171,81,120,167,82,83 ...
        return baseAttrInfoMapper.selectAttrInfoListByIds(attrValueIds);

    }

    // 判断集合是否为空！
    // 泛型方法
//    public <T>  boolean checkListIsEmpty(ArrayList<T> list){
//        if (list!=null && list.size()>0){
//            return true;
//        }
//        return false;
//    }
    public <T> boolean checkListIsEmpty(List<T> list) {
        if (list != null && list.size() > 0) {
            return true;
        }
        return false;
    }
}
