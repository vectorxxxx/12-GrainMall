package xyz.funnyboy.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboy.gulimall.cart.constant.CartConstant;
import xyz.funnyboy.gulimall.cart.feign.ProductFeignService;
import xyz.funnyboy.gulimall.cart.interceptor.CartInterceptor;
import xyz.funnyboy.gulimall.cart.service.CartService;
import xyz.funnyboy.gulimall.cart.vo.Cart;
import xyz.funnyboy.gulimall.cart.vo.CartItem;
import xyz.funnyboy.gulimall.cart.vo.SkuInfoTO;
import xyz.funnyboy.gulimall.cart.vo.UserInfoTo;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-08 10:29:51
 */
@Service
@Slf4j
public class CartServiceImpl implements CartService
{
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        final BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        final String res = (String) cartOps.get(skuId.toString());

        if (StringUtils.isEmpty(res)) {
            CartItem cartItem = new CartItem();

            // 1、商品基本信息
            final CompletableFuture<Void> skuInfoFuture = CompletableFuture.runAsync(() -> {
                final R skuInfo = productFeignService.getSkuInfo(skuId);
                final SkuInfoTO skuInfoTO = skuInfo.getData("skuInfo", new TypeReference<SkuInfoTO>() {});
                cartItem.setSkuId(skuInfoTO.getSkuId());
                cartItem.setCheck(true);
                cartItem.setTitle(skuInfoTO.getSkuTitle());
                cartItem.setImage(skuInfoTO.getSkuDefaultImg());
                cartItem.setPrice(skuInfoTO.getPrice());
                cartItem.setCount(num);
            }, executor);

            // 2、商品属性信息
            final CompletableFuture<Void> attrFuture = CompletableFuture.runAsync(() -> {
                final List<String> attrValues = productFeignService.getSkuSaleAttrValues(skuId);
                cartItem.setSkuAttr(attrValues);
            }, executor);

            // 3、以上两个线程成功后给redis中存放数据
            CompletableFuture
                    .allOf(skuInfoFuture, attrFuture)
                    .get();
            cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
            return cartItem;
        }
        // 已有商品商品
        else {
            CartItem cartItem = JSON.parseObject(res, CartItem.class);
            cartItem.setCount(cartItem.getCount() + num);
            cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
            return cartItem;
        }
    }

    @Override
    public CartItem getCartItem(Long skuId) {
        final BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        final String res = (String) cartOps.get(skuId.toString());
        return JSON.parseObject(res, CartItem.class);
    }

    private BoundHashOperations<String, Object, Object> getCartOps() {
        final UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();
        String cartKey;
        // 已登录
        if (userInfoTo.getUserId() != null) {
            cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserId();
        }
        // 未登录
        else {
            cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserKey();
        }

        return redisTemplate.boundHashOps(cartKey);
    }

    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {
        Cart cart = new Cart();
        final UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();
        // 已登录
        if (userInfoTo.getUserId() != null) {
            // 临时购物车合并至正式购物车
            final String tempCartKey = CartConstant.CART_PREFIX + userInfoTo.getUserKey();
            final List<CartItem> tempCartItemList = getCartItems(tempCartKey);
            for (CartItem item : tempCartItemList) {
                addToCart(item.getSkuId(), item.getCount());
            }
            clearCart(tempCartKey);

            // 获取正式购物车
            final String cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserId();
            final List<CartItem> cartItemList = getCartItems(cartKey);
            cart.setItems(cartItemList);
        }
        // 未登录
        else {
            // 获取临时购物车
            final String cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserKey();
            final List<CartItem> cartItemList = getCartItems(cartKey);
            cart.setItems(cartItemList);
        }
        return cart;
    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        final BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        final CartItem cartItem = JSON.parseObject((String) cartOps.get(skuId.toString()), CartItem.class);
        Objects
                .requireNonNull(cartItem)
                .setCheck(check == 1);
        cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
    }

    @Override
    public void countItem(Long skuId, Integer num) {
        final BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        final CartItem cartItem = JSON.parseObject((String) cartOps.get(skuId.toString()), CartItem.class);
        Objects
                .requireNonNull(cartItem)
                .setCount(num);
        cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
    }

    @Override
    public void deleteItem(Long skuId) {
        final BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    @Override
    public List<CartItem> getCurrentUserCartItems() {
        final UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();
        if (userInfoTo.getUserId() == null) {
            return null;
        }

        return
                // 获取当前用户购物车
                getCartItems(CartConstant.CART_PREFIX + userInfoTo.getUserId())
                        .stream()
                        // 过滤所有被选中的购物项
                        .filter(CartItem::getCheck)
                        // 设置价格
                        .peek(item -> {
                            BigDecimal price = productFeignService.getPrice(item.getSkuId());
                            item.setPrice(price);
                        })
                        .collect(Collectors.toList());

    }

    private List<CartItem> getCartItems(String cartKey) {
        final BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(cartKey);
        final List<Object> values = ops.values();
        return Optional
                .ofNullable(values)
                .orElse(Collections.emptyList())
                .stream()
                .map(item -> JSON.parseObject((String) item, CartItem.class))
                .collect(Collectors.toList());
    }

    /**
     * 清除购物车
     *
     * @param cartKey 购物车key
     */
    private void clearCart(String cartKey) {
        redisTemplate.delete(cartKey);
    }
}
