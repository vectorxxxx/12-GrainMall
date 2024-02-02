package xyz.funnyboy.gulimall.product.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * SPU保存信息
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-02-02 09:40:40
 */
@Data
public class SpuSaveVo
{

    private String spuName;

    private String spuDescription;

    private Long catalogId;

    private Long brandId;

    private BigDecimal weight;

    private int publishStatus;

    /**
     * 表述图片
     */
    private List<String> decript;

    private List<String> images;

    private Bounds bounds;

    private List<BaseAttrs> baseAttrs;

    private List<Skus> skus;
}
