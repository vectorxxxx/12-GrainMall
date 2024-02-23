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
public class Catelog2VO implements Serializable
{
    private static final long serialVersionUID = -6641472202687699564L;
    private String id;
    private String name;
    private String catelog1Id;
    private List<Catelog3VO> catelog3List;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Catelog3VO implements Serializable
    {
        private static final long serialVersionUID = 8329497023658925759L;
        private String id;
        private String name;
        private String catelog2Id;
    }
}
