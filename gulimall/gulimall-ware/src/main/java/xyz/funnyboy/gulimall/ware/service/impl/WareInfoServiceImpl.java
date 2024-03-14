package xyz.funnyboy.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.Query;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboy.gulimall.ware.dao.WareInfoDao;
import xyz.funnyboy.gulimall.ware.entity.WareInfoEntity;
import xyz.funnyboy.gulimall.ware.feign.MemberFeignService;
import xyz.funnyboy.gulimall.ware.service.WareInfoService;
import xyz.funnyboy.gulimall.ware.vo.FareVO;
import xyz.funnyboy.gulimall.ware.vo.MemberAddressVO;

import java.math.BigDecimal;
import java.util.Map;

@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService
{

    @Autowired
    private MemberFeignService memberFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareInfoEntity> page = this.page(new Query<WareInfoEntity>().getPage(params), new QueryWrapper<WareInfoEntity>());

        return new PageUtils(page);
    }

    /**
     * 按条件查询页面
     *
     * @param params 参数
     * @return {@link PageUtils}
     */
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        // 获取参数
        final String key = (String) params.get("key");

        // 查询条件
        final LambdaQueryWrapper<WareInfoEntity> queryWrapper = new LambdaQueryWrapper<WareInfoEntity>();
        if (!StringUtils.isEmpty(key)) {
            queryWrapper
                    .eq(WareInfoEntity::getId, key)
                    .or()
                    .like(WareInfoEntity::getName, key)
                    .or()
                    .like(WareInfoEntity::getAddress, key)
                    .or()
                    .like(WareInfoEntity::getAreacode, key);
        }

        // 分页查询
        final IPage<WareInfoEntity> page = baseMapper.selectPage(new Query<WareInfoEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }

    @Override
    public FareVO getFare(Long addrId) {

        final R r = memberFeignService.addrInfo(addrId);
        final MemberAddressVO memberAddressVO = r.getData("memberReceiveAddress", new TypeReference<MemberAddressVO>() {});
        if (memberAddressVO == null) {
            return null;
        }

        // 简略计算运费，用手机号后前两位作为运费
        final String fareStr = memberAddressVO
                .getPhone()
                .substring(0, 2);
        final BigDecimal fare = new BigDecimal(fareStr);

        // 封装运费信息
        final FareVO fareVO = new FareVO();
        fareVO.setAddress(memberAddressVO);
        fareVO.setFare(fare);
        return fareVO;
    }

}
