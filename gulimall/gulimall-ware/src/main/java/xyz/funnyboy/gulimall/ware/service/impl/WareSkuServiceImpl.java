package xyz.funnyboy.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.Query;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboy.gulimall.ware.dao.WareSkuDao;
import xyz.funnyboy.gulimall.ware.entity.WareSkuEntity;
import xyz.funnyboy.gulimall.ware.feign.ProductFeignService;
import xyz.funnyboy.gulimall.ware.service.WareSkuService;

import java.util.Map;

@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService
{
    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        final String wareId = (String) params.get("wareId");
        final String skuId = (String) params.get("skuId");
        final LambdaQueryWrapper<WareSkuEntity> queryWrapper = new LambdaQueryWrapper<WareSkuEntity>()
                .eq(!StringUtils.isEmpty(wareId), WareSkuEntity::getWareId, wareId)
                .eq(!StringUtils.isEmpty(skuId), WareSkuEntity::getSkuId, skuId);
        IPage<WareSkuEntity> page = this.page(new Query<WareSkuEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }

    @Override
    public double addStock(Long wareId, Long skuId, Integer skuNum) {
        // 查询商品库存数据
        WareSkuEntity wareSkuEntity = baseMapper.selectOne(new LambdaQueryWrapper<WareSkuEntity>()
                .eq(WareSkuEntity::getWareId, wareId)
                .eq(WareSkuEntity::getSkuId, skuId));

        // 查询商品名称和价格
        String skuName = "";
        double price = 0;
        final R info = productFeignService.info(skuId);
        if (info.getCode() == 0) {
            final Map<String, Object> data = (Map<String, Object>) info.get("skuInfo");
            skuName = (String) data.get("skuName");
            price = (Double) data.get("price");
        }

        // 有则更新，无则新增
        if (wareSkuEntity == null) {
            wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setStock(skuNum);
        }
        else {
            wareSkuEntity.setStock(wareSkuEntity.getStock() + skuNum);
        }
        wareSkuEntity.setWareId(wareId);
        wareSkuEntity.setSkuId(skuId);
        wareSkuEntity.setSkuName(skuName);
        wareSkuEntity.setStockLocked(0);
        this.saveOrUpdate(wareSkuEntity);
        return price;
    }

}
