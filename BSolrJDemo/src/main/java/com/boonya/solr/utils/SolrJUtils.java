package com.boonya.solr.utils;

import java.io.IOException;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;

/**
 * SolrJ接口类使用工具
 * 
 * @packge com.boonya.solr.utils.SolrUtils
 * @date 2017年2月25日 下午5:32:19
 * @author pengjunlin
 * @comment
 * @update
 */
public class SolrJUtils {

	public static final String solrBaseUrl = "http://localhost:8983/solr/";

	private static HttpSolrClient init(HttpSolrClient httpSolrClient)
			throws SolrServerException, IOException {
		httpSolrClient.setConnectionTimeout(100);
		httpSolrClient.setDefaultMaxConnectionsPerHost(100);
		httpSolrClient.setMaxTotalConnections(100);
		return httpSolrClient;
	}

	@SuppressWarnings("deprecation")
	public static SolrClient getClient() throws SolrServerException,
			IOException {
		HttpSolrClient httpSolrClient = new HttpSolrClient(solrBaseUrl);// early
																	// version
		init(httpSolrClient);
		return httpSolrClient;
	}

	@SuppressWarnings("deprecation")
	public static SolrClient getClient(String solrUrl)
			throws SolrServerException, IOException {
		HttpSolrClient httpSolrClient = new HttpSolrClient(solrUrl);// early
																	// version
		init(httpSolrClient);
		return httpSolrClient;
	}

	public static SolrClient buildClient() throws SolrServerException,
			IOException {

		return new HttpSolrClient.Builder(solrBaseUrl).build();// recently version
	}

	public static SolrClient buildClient(String solrUrl)
			throws SolrServerException, IOException {

		return new HttpSolrClient.Builder(solrUrl).build();// recently version
	}

	public static QueryResponse query(String keyword)
			throws SolrServerException, IOException {
		return SolrJUtils.buildClient().query(new SolrQuery(keyword));// keyword="*:*"
	}

	public static QueryResponse query(String core, String keyword)
			throws SolrServerException, IOException {
		return SolrJUtils.buildClient().query(new SolrQuery(core, keyword));// keyword="*:*"
	}

	public static QueryResponse query(SolrQuery solrQuery)
			throws SolrServerException, IOException {
		return SolrJUtils.buildClient().query(solrQuery);
	}

	public static QueryResponse query(String solrUrl, String core,
			String keyword) throws SolrServerException, IOException {
		return SolrJUtils.buildClient(solrUrl).query(
				new SolrQuery(core, keyword));// keyword="*:*"
	}

	public static QueryResponse query(String solrUrl, SolrQuery solrQuery)
			throws SolrServerException, IOException {
		return SolrJUtils.buildClient(solrUrl).query(solrQuery);
	}


}
