package com.zc.esjd.controller;

import com.zc.esjd.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author Corey Zhang
 * @create 2020-05-04 23:31
 */
@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/parse/{keyword}")
    public String GetJDProductByKeyword(@PathVariable("keyword") String keyword) throws Exception {
        return productService.parseProduct(keyword).toString();
    }


    @GetMapping("/search/{keyword}/{pageNo}/{pageSize}")
    public List<Map<String, Object>> searchKeywordByPage(@PathVariable("keyword") String keyword,
                                                         @PathVariable("pageNo") int pageNo,
                                                         @PathVariable("pageSize") int pageSize) throws IOException {

        if (pageNo <= 1){
            pageNo = 1;
        }

        if (pageSize <= 5){
            pageSize = 5;
        }

        return productService.getProducts(keyword, pageNo, pageSize);
    }


    @GetMapping("/search/high/{keyword}/{pageNo}/{pageSize}")
    public List<Map<String, Object>> searchHighLightKeywordByPage(@PathVariable("keyword") String keyword,
                                                         @PathVariable("pageNo") int pageNo,
                                                         @PathVariable("pageSize") int pageSize) throws IOException {

        if (pageNo <= 1){
            pageNo = 1;
        }

        if (pageSize <= 5){
            pageSize = 5;
        }

        return productService.getHighLightProducts(keyword, pageNo, pageSize);
    }
}
