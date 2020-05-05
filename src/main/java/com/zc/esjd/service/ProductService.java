package com.zc.esjd.service;

import com.alibaba.fastjson.JSON;
import com.zc.esjd.content.Content;
import com.zc.esjd.model.Product;
import com.zc.esjd.utils.HtmlParseUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author Corey Zhang
 * @create 2020-05-04 23:31
 */
@Service
public class ProductService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public Boolean parseProduct(String keyword) throws Exception {
        List<Product> products = new HtmlParseUtils().parseJD(keyword);

        //Get bulk rquest.
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("60s");

        for (Product product:products){
            bulkRequest.add(
                    new IndexRequest(Content.JD_INDEX)
                    .source(JSON.toJSONString(product), XContentType.JSON)
            );

        }

        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);

        return !bulkResponse.hasFailures();
    }


    public List<Map<String, Object>> getProducts(String keyword, int pageNo, int pageSize) throws IOException {

        List<Map<String, Object>> ret = new ArrayList<>();

        SearchRequest searchRequest = new SearchRequest(Content.JD_INDEX);

        //Build the search criteria.
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));


        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", keyword);

        //Set searchSource.
        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.from(pageNo);
        searchSourceBuilder.size(pageSize);


        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        for (SearchHit document: searchResponse.getHits().getHits()){
            Map<String, Object> sourceAsMap = document.getSourceAsMap();
            ret.add(sourceAsMap);

        }

        return ret;

    }



    public List<Map<String, Object>> getHighLightProducts(String keyword, int pageNo, int pageSize) throws IOException {

        List<Map<String, Object>> ret = new ArrayList<>();

        SearchRequest searchRequest = new SearchRequest(Content.JD_INDEX);

        //Build the search criteria.
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));


        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", keyword);

        //Set searchSource.
        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.from(pageNo);
        searchSourceBuilder.size(pageSize);

        //Set High light
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);


        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        for (SearchHit document: searchResponse.getHits().getHits()){
            Map<String, HighlightField> highlightFields = document.getHighlightFields();
            HighlightField title = highlightFields.get("title");
            Map<String, Object> documentSourceAsMap = document.getSourceAsMap();

            if (title != null){
                Text[] fragments = title.fragments();
                String newTitle = "";
                for (Text text : fragments){
                    newTitle += text;
                }

                documentSourceAsMap.put("title", newTitle);

            }


            ret.add(documentSourceAsMap);

        }

        return ret;

    }
}
