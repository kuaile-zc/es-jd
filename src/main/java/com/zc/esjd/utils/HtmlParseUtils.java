package com.zc.esjd.utils;

import com.zc.esjd.model.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 *
 * @author Corey Zhang
 * @create 2020-05-04 22:58
 */
public class HtmlParseUtils {
    public static void main(String[] args) throws Exception {
        new HtmlParseUtils().parseJD("Java").forEach(System.out::println);
    }

    public List<Product> parseJD(String keywords) throws Exception {

        ArrayList<Product> ret = new ArrayList<>();
        //Get Url https://search.jd.com/Search?keyword=java
        //Need connected networking.
        String url = "https://search.jd.com/Search?keyword="+keywords;
        //解析网页
        Document document = Jsoup.parse(new URL(url), 30000);
        //获取所有的elements
        Element element = document.getElementById("J_goodsList");
        //获取所有li元素
        Elements elements = element.getElementsByTag("li");

        for (Element el : elements){
            String img = el.getElementsByTag("img").eq(0).attr("src");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();

            Product product = new Product(price, title, img);
            ret.add(product);

        }
        return ret;
    }
}
