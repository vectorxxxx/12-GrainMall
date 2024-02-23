package xyz.funnyboy.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-02-23 13:52:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catalog2VO implements Serializable
{
    private static final long serialVersionUID = -6641472202687699564L;
    private String catalog1Id;
    private List<Catalog3VO> catalog3List;
    private String id;
    private String name;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Catalog3VO implements Serializable
    {
        private static final long serialVersionUID = 8329497023658925759L;
        private String catalog2Id;
        private String id;
        private String name;
    }
}
