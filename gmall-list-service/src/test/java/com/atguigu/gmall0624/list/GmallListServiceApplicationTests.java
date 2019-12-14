package com.atguigu.gmall0624.list;

import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallListServiceApplicationTests {

	@Autowired
	private JestClient jestClient;
	@Test
	public void contextLoads() {
	}

	@Test
	public void testES() throws IOException {
		// 获取es 中的数据！
		/*
			GET movie_chn/movie/_search
			{
			  "query": {
				"match": {
				  "actorList.name": "张译"
				}
			  }
			}
			1.	定义dsl 语句
			2.	定义查询的动作
			3.	准备执行
			4.	获取返回结果
		 */
		String query = "{\n" +
				"  \"query\": {\n" +
				"    \"match\": {\n" +
				"      \"actorList.name\": \"张译\"\n" +
				"    }\n" +
				"  }\n" +
				"}";
		// 定义查询的动作
		Search search = new Search.Builder(query).addIndex("movie_chn").addType("movie").build();
		// 执行
		SearchResult searchResult = jestClient.execute(search);

		Long total = searchResult.getTotal();
		System.out.println("记实数:"+total);
		// 从searchResult 获取结果
		List<SearchResult.Hit<Map, Void>> hits = searchResult.getHits(Map.class);
		for (SearchResult.Hit<Map, Void> hit : hits) {
			Map map = hit.source;
			System.out.println(map.get("name"));
		}


	}

}
