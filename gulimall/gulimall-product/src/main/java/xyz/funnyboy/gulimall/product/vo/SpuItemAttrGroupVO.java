package xyz.funnyboy.gulimall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-04 22:38:40
 */
@Data
@ToString
public class SpuItemAttrGroupVO
{
    private String groupName;
    private List<Attr> attrs;
}
