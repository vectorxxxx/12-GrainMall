package xyz.funnyboy.gulimall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.Query;
import xyz.funnyboy.gulimall.member.dao.MemberDao;
import xyz.funnyboy.gulimall.member.entity.MemberEntity;
import xyz.funnyboy.gulimall.member.exception.PhoneExistException;
import xyz.funnyboy.gulimall.member.exception.UsernameExistException;
import xyz.funnyboy.gulimall.member.service.MemberLevelService;
import xyz.funnyboy.gulimall.member.service.MemberService;
import xyz.funnyboy.gulimall.member.vo.MemberLoginVO;
import xyz.funnyboy.gulimall.member.vo.MemberRegistVO;

import java.util.Date;
import java.util.Map;

@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService
{
    @Autowired
    private MemberLevelService memberLevelService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(new Query<MemberEntity>().getPage(params), new QueryWrapper<MemberEntity>());

        return new PageUtils(page);
    }

    @Override
    public void register(MemberRegistVO memberRegistVO) {
        checkPhoneUnique(memberRegistVO.getPhone());
        checkUserNameUnique(memberRegistVO.getUsername());

        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setLevelId(memberLevelService.getDefaultLevel());
        memberEntity.setUsername(memberRegistVO.getUsername());
        memberEntity.setPassword(new BCryptPasswordEncoder().encode(memberRegistVO.getPassword()));
        memberEntity.setNickname(memberRegistVO.getUsername());
        memberEntity.setMobile(memberRegistVO.getPhone());
        memberEntity.setIntegration(0);
        memberEntity.setGrowth(0);
        memberEntity.setStatus(0);
        memberEntity.setCreateTime(new Date());

        baseMapper.insert(memberEntity);
    }

    @Override
    public MemberEntity login(MemberLoginVO vo) {
        final String loginacct = vo.getLoginacct();
        final MemberEntity memberEntity = baseMapper.selectOne(new LambdaQueryWrapper<MemberEntity>()
                .eq(MemberEntity::getUsername, loginacct)
                .or()
                .eq(MemberEntity::getMobile, loginacct));
        if (memberEntity == null) {
            return null;
        }

        if (!new BCryptPasswordEncoder().matches(vo.getPassword(), memberEntity.getPassword())) {
            return null;
        }

        return memberEntity;
    }

    /**
     * 检查手机唯一性
     *
     * @param phone 电话
     */
    private void checkPhoneUnique(String phone) {
        final int count = this.count(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getMobile, phone));
        if (count > 0) {
            throw new PhoneExistException();
        }
    }

    /**
     * 检查用户名唯一性
     *
     * @param userName 用户名
     */
    private void checkUserNameUnique(String userName) {
        final int count = this.count(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getUsername, userName));
        if (count > 0) {
            throw new UsernameExistException();
        }
    }
}
