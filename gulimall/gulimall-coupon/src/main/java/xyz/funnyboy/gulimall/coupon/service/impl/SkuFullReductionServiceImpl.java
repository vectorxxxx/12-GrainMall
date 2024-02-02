package xyz.funnyboy.gulimall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.funnyboy.common.to.SkuReductionTO;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.Query;
import xyz.funnyboy.gulimall.coupon.dao.SkuFullReductionDao;
import xyz.funnyboy.gulimall.coupon.entity.MemberPriceEntity;
import xyz.funnyboy.gulimall.coupon.entity.SkuFullReductionEntity;
import xyz.funnyboy.gulimall.coupon.entity.SkuLadderEntity;
import xyz.funnyboy.gulimall.coupon.service.MemberPriceService;
import xyz.funnyboy.gulimall.coupon.service.SkuFullReductionService;
import xyz.funnyboy.gulimall.coupon.service.SkuLadderService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService
{
    @Autowired
    private SkuLadderService skuLadderService;

    @Autowired
    private SkuFullReductionService skuFullReductionService;

    @Autowired
    private MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(new Query<SkuFullReductionEntity>().getPage(params), new QueryWrapper<SkuFullReductionEntity>());

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionTO skuReductionTO) {
        // 1、保存满减打折，会员价 sms_sku_ladder
        // 有的满减条件才保存
        if (skuReductionTO.getFullCount() > 0) {
            SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
            BeanUtils.copyProperties(skuReductionTO, skuLadderEntity);
            skuLadderEntity.setAddOther(skuReductionTO.getCountStatus());
            skuLadderService.save(skuLadderEntity);
        }

        // 2、保存满减信息 sms_sku_full_reduction
        if (skuReductionTO
                .getFullPrice()
                .compareTo(BigDecimal.ZERO) > 0) {
            SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
            BeanUtils.copyProperties(skuReductionTO, skuFullReductionEntity);
            skuFullReductionEntity.setAddOther(skuReductionTO.getPriceStatus());
            skuFullReductionService.save(skuFullReductionEntity);
        }

        // 3、保存会员价格 sms_member_price
        final List<MemberPriceEntity> memberPriceEntityList = skuReductionTO
                .getMemberPrice()
                .stream()
                .map(memberPrice -> {
                    MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
                    memberPriceEntity.setSkuId(skuReductionTO.getSkuId());
                    memberPriceEntity.setMemberLevelId(memberPrice.getId());
                    memberPriceEntity.setMemberLevelName(memberPrice.getName());
                    memberPriceEntity.setMemberPrice(memberPrice.getPrice());
                    memberPriceEntity.setAddOther(1);
                    return memberPriceEntity;
                })
                .filter(memberPrice -> memberPrice
                        .getMemberPrice()
                        .compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());
        memberPriceService.saveBatch(memberPriceEntityList);
    }

}
