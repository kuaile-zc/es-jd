package com.zc.esjd.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:
 *
 * @author Corey Zhang
 * @create 2020-05-04 23:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private String price;
    private String title;
    private String img;
}
