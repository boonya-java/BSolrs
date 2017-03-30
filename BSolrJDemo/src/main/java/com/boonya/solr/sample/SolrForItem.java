package com.boonya.solr.sample;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;

import com.boonya.solr.utils.SolrJUtils;
/**
 * 参考:http://blog.csdn.net/itbasketplayer/article/details/8086160
 * @packge com.boonya.solr.utils.ItemSolr
 * @date   2017年2月25日  下午6:47:45
 * @author pengjunlin
 * @comment   
 * @update
 */
public class SolrForItem {
	
	private static SolrClient solr =null;
	
	static{
		try {
			solr=SolrJUtils.buildClient(SolrJUtils.solrBaseUrl+"item");
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 验证ID是否为空
	 * 
	 * @MethodName: checkItem
	 * @Description:
	 * @param item
	 * @return
	 * @throws
	 */
	private boolean checkItem(Item item) {
		return item == null ? false : (item.getId()==0 ? false : true);
	}

	/**
	 * @throws IOException
	 * @throws SolrServerException
	 *             添加索引
	 * 
	 * @MethodName: addIndex
	 * @Description:
	 * @param list
	 * @throws
	 */
	public void addIndex(List<Item> list) throws SolrServerException,
			IOException {
		
		try {
			Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
			for (int i = 0; i < list.size(); i++) {
				Item item = list.get(i);
				// 设置每个字段不得为空，可以在提交索引前进行检查
				if (checkItem(item)) {
					SolrInputDocument doc = new SolrInputDocument();
					// 在这里请注意date的格式，要进行适当的转化，上文已提到
					doc.addField("id", item.getId());
					doc.addField("name", item.getName());
					doc.addField("price", item.getPrice());
					docs.add(doc);
				}
			}
			solr.add(docs);
			// 使用数据创建bean对象列表
			// solr.addBeans(docs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 对索引进行优化
			solr.optimize();

			solr.commit();
		}
	}

	/**
	 * 删除所有的索引
	 * 
	 * @MethodName: deleteAllIndex
	 * @Description:
	 * @throws SolrServerException
	 * @throws IOException
	 * @throws
	 */
	public void deleteAllIndex() throws SolrServerException, IOException {
		try {
			// 删除所有的索引
			solr.deleteByQuery("*:*");
			solr.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据索引号删除索引
	 * 
	 * @MethodName: deleteIndexById
	 * @Description:
	 * @param id
	 * @throws SolrServerException
	 * @throws IOException
	 * @throws
	 */
	public void deleteIndexById(String id) throws SolrServerException,
			IOException {
		try {
			// 根据索引号删除索引：
			solr.deleteById(id);
			solr.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @throws IOException
	 * @throws SolrServerException
	 *             查询：SolrJ提供的查询功能比较强大，可以进行结果中查询、范围查询、排序等
	 * 
	 * @MethodName: query
	 * @Description:
	 * @param field
	 * @param key
	 * @param start
	 * @param count
	 * @param sortfield
	 * @param flag
	 * @param hightlight
	 * @return
	 * @throws
	 */
	public static QueryResponse query(String[] field, String[] key, int start,
			int count, String[] sortfield, Boolean[] flag, Boolean hightlight)
			throws SolrServerException, IOException {
		// 检测输入是否合法
		if (null == field || null == key || field.length != key.length) {
			return null;
		}

		if (null == sortfield || null == flag
				|| sortfield.length != flag.length) {
			return null;
		}

		SolrQuery query = null;
		try {

			// 初始化查询对象
			query = new SolrQuery(field[0] + ":" + key[0]);
			for (int i = 0; i < field.length; i++) {
				query.addFilterQuery(field[i] + ":" + key[i]);
			}

			// 设置起始位置与返回结果数
			query.setStart(start);
			query.setRows(count);
			// 设置排序
			for (int i = 0; i < sortfield.length; i++) {
				if (flag[i]) {
					query.addSort(sortfield[i], SolrQuery.ORDER.asc);
				} else {
					query.addSort(sortfield[i], SolrQuery.ORDER.desc);
				}
			}

			// 设置高亮
			if (null != hightlight) {
				query.setHighlight(true); // 开启高亮组件
				query.addHighlightField("title");// 高亮字段
				query.setHighlightSimplePre("<font color='red'>");// 标记
				query.setHighlightSimplePost("</font>");
				query.setHighlightSnippets(1);// 结果分片数，默认为1
				query.setHighlightFragsize(1000);// 每个分片的最大长度，默认为100
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		QueryResponse rsp = null;
		try {
			rsp = solr.query(query);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		// 返回查询结果
		return rsp;
	}

	public void readResult(QueryResponse rsp,String[] field, String[] key, String[] sortfield, Boolean[] flag) throws SolrServerException, IOException {
		// DocList读取返回结果：
		//SolrDocumentList solrList = rsp.getResults();

		// Beans读取返回结果：
		//List<Item> tmpLists = rsp.getBeans(Item.class);

		// 读取高亮显示结果：
		rsp = query(field, key, 0, 10, sortfield, flag, true);
		if (null == rsp) {
			return ;
		}
		Map<String, Map<String, List<String>>> hightlight = rsp
				.getHighlighting();
		
		//Item即为上面定义的bean类
        List<Item> tmpLists = rsp.getBeans(Item.class);
        for (int i = 0; i < tmpLists.size(); i++) {
               //hightlight的键为Item的id，值唯一，我们设置的高亮字段为title
               String hlString = hightlight.get(tmpLists.get(i).getId()).get("title").toString();
               if (null != hlString) {
                      System.out.println(hlString);
               }
        }

	}
	
	

	/**
	 * Facet的一个应用：自动补全
	 * 
	 * @MethodName: autoComplete 
	 * @Description: 
	 * @param prefix
	 * @param min
	 * @return
	 * @throws SolrServerException
	 * @throws IOException
	 * @throws
	 */
    //prefix为前缀，min为最大返回结果数
	public static String[] autoComplete(String prefix, int min) throws SolrServerException, IOException {
        String words[] = null;
        StringBuffer sb = new StringBuffer("");
        SolrQuery query = new SolrQuery("*.*");
        QueryResponse rsp= new QueryResponse();
        //Facet为solr中的层次分类查询
        try {
               query.setFacet(true);
               query.setQuery("*:*");
               query.setFacetPrefix(prefix);
               query.addFacetField("title");
               rsp = solr.query(query);

        } catch (Exception e) {
               e.printStackTrace();
               return null;
        }

        if(null != rsp){
               FacetField ff = rsp.getFacetField("title");
               List<Count> countList = ff.getValues();
               if(null == countList){
                      return null;
               }
               for(int i=0; i<countList.size(); i++){
                      String tmp[] = countList.get(i).toString().split(" ");
                      //排除单个字
                      if(tmp[0].length()< 2){
                             continue;
                      }
                      sb.append(tmp[0] + " ");
                      min--;
                      if(min == 0){
                             break;
                      }
               }
               words = sb.toString().split(" ");
        }else{
               return null;
        }
        return words;
    }

}

