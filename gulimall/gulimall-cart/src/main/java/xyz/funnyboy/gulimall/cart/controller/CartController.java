package xyz.funnyboy.gulimall.cart.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import xyz.funnyboy.gulimall.cart.service.CartService;
import xyz.funnyboy.gulimall.cart.vo.Cart;
import xyz.funnyboy.gulimall.cart.vo.CartItem;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-08 09:25:54
 */
@Controller
@Slf4j
public class CartController
{
    @Autowired
    private CartService cartService;

    @ResponseBody
    @GetMapping("currentUserCartItems")
    public List<CartItem> getCurrentUserCartItems() {
        return cartService.getCurrentUserCartItems();
    }

    // @GetMapping("/cart.html")
    // public String cart() {
    //     final UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();
    //     log.info("用户信息：{}", userInfoTo);
    //     // ①不登录
    //     // 用户信息：UserInfoTo(userId=null, userKey=74a7bcdd-818f-4791-8d3a-e9bcce05111e, tempUser=false)
    //     // 用户信息：UserInfoTo(userId=null, userKey=74a7bcdd-818f-4791-8d3a-e9bcce05111e, tempUser=true)
    //     // ②已登录
    //     // 用户信息：UserInfoTo(userId=3, userKey=74a7bcdd-818f-4791-8d3a-e9bcce05111e, tempUser=true)
    //     return "cartList";
    // }

    /**
     * 我的购物车
     *
     * @param model 型
     * @return {@link String}
     * @throws ExecutionException   执行异常
     * @throws InterruptedException 中断异常
     */
    @GetMapping("/cart.html")
    public String cart(Model model) throws ExecutionException, InterruptedException {
        Cart cart = cartService.getCart();
        model.addAttribute("cart", cart);
        return "cartList";
    }

    /**
     * 加入购物车
     *
     * @param skuId              SKU ID
     * @param num                数量
     * @param redirectAttributes 重定向属性
     * @return {@link String}
     * @throws ExecutionException   执行异常
     * @throws InterruptedException 中断异常
     */
    @GetMapping("/addToCart")
    public String addToCart(
            @RequestParam("skuId")
                    Long skuId,
            @RequestParam("num")
                    Integer num, RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {
        CartItem cartItem = cartService.addToCart(skuId, num);
        redirectAttributes.addAttribute("skuId", cartItem.getSkuId());
        return "redirect://cart.gulimall.com/addToCartSuccess.html";
    }

    /**
     * 加入购物车成功
     *
     * @param skuId SKU ID
     * @param model 型
     * @return {@link String}
     */
    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccess(
            @RequestParam("skuId")
                    Long skuId, Model model) {
        CartItem cartItem = cartService.getCartItem(skuId);
        model.addAttribute("item", cartItem);
        return "success";
    }

    /**
     * 勾选商品
     *
     * @param skuId SKU ID
     * @param check 检查
     * @return {@link String}
     */
    @GetMapping("/checkItem")
    public String checkItem(
            @RequestParam("skuId")
                    Long skuId,
            @RequestParam("check")
                    Integer check) {
        cartService.checkItem(skuId, check);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    /**
     * 商品计数
     *
     * @param skuId SKU ID
     * @param num   数量
     * @return {@link String}
     */
    @GetMapping("/countItem")
    public String countItem(
            @RequestParam("skuId")
                    Long skuId,
            @RequestParam("num")
                    Integer num) {
        cartService.countItem(skuId, num);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    /**
     * 删除商品
     *
     * @param skuId SKU ID
     * @return {@link String}
     */
    @GetMapping("/deleteItem")
    public String deleteItem(
            @RequestParam("skuId")
                    Long skuId) {
        cartService.deleteItem(skuId);
        return "redirect:http://cart.gulimall.com/cart.html";
    }
}
