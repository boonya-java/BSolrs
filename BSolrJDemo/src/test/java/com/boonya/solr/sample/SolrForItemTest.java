package com.boonya.solr.sample;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;

/**
 * Item测试
 * 
 * @packge com.boonya.solr.sample.SolrForItemTest
 * @date 2017年2月26日 下午5:57:12
 * @author pengjunlin
 * @comment
 * @update
 */
public class SolrForItemTest {

	private String[] products = new String[] { "河南杜康酒", "川酒五粮液", "川酒剑南春", "国酒茅台酒","山西汾酒杏花村" };

	@Test
	public void testAddIndex() {
		SolrForItem solrForItem = new SolrForItem();
		List<Item> list = new ArrayList<Item>();
		Item item = null;
		for (int i = 0; i < products.length; i++) {
			double price = Double.valueOf(Math.random()*1000+""); //1000以内数字
			int toIntPrice=(int) price;
			item = new Item(i + 1, products[i], toIntPrice);
			list.add(item);
		}
		try {
			solrForItem.addIndex(list);// 添加索引
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		}
	}

}
