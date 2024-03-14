package xyz.funnyboy.gulimall.cart.service;

import xyz.funnyboy.gulimall.cart.vo.Cart;
import xyz.funnyboy.gulimall.cart.vo.CartItem;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-08 10:29:10
 */
public interface CartService
{

    /**
     * 加入购物车
     *
     * @param skuId SKU ID
     * @param num   数量
     * @return {@link CartItem}
     */
    CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    /**
     * 获取购物车商品
     *
     * @param skuId SKU ID
     * @return {@link CartItem}
     */
    CartItem getCartItem(Long skuId);

    /**
     * 获取购物车
     *
     * @return {@link Cart}
     */
    Cart getCart() throws ExecutionException, InterruptedException;

    /**
     * 勾选商品
     *
     * @param skuId SKU ID
     * @param check 检查
     */
    void checkItem(Long skuId, Integer check);

    /**
     * 商品计数
     *
     * @param skuId SKU ID
     * @param num   数量
     */
    void countItem(Long skuId, Integer num);

    /**
     * 删除商品
     *
     * @param skuId SKU ID
     */
    void deleteItem(Long skuId);

    /**
     * 获取当前用户购物车商品
     *
     * @return {@link List}<{@link CartItem}>
     */
    List<CartItem> getCurrentUserCartItems();
}
