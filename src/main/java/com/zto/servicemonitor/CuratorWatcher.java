package com.zto.servicemonitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

/**
 * 监控子节点变化，返回节点内容改变后的想
 * 
 * @author yaoyuxiao
 *
 */
@RestController
public class CuratorWatcher {
	private static final String ZK_ADDRESS = "10.10.19.47:2181";
	private static final String ZK_PATH = "/";
	private static CuratorFramework client;

	@Autowired
	private CacheValue cacheValue;

	@RequestMapping("/watcher")
	public void watcher() throws Exception {
		client = CuratorFrameworkFactory.newClient(ZK_ADDRESS, new RetryNTimes(
				10, 500000));
		client.start();
		ExecutorService pool = Executors.newFixedThreadPool(2);
		final TreeCache treeCache = new TreeCache(client, ZK_PATH);
		treeCache.getListenable().addListener(new TreeCacheListener() {
			@Override
			public void childEvent(CuratorFramework client, TreeCacheEvent event)
					throws Exception {
				switch (event.getType()) {
				case NODE_ADDED:
					String strList1 = cacheValue.giveValue(client, ZK_PATH);
					String res1 = sendPost("http://10.10.42.107/updateConfig",
							strList1);
					System.out.println("返回结果：" + res1);
					break;
				case NODE_REMOVED:
					String strList2 = cacheValue.giveValue(client, ZK_PATH);
					String res2 = sendPost("http://10.10.42.107/updateConfig",
							strList2);
					System.out.println("返回结果：" + res2);
					break;
				case NODE_UPDATED:
					String strList3 = cacheValue.giveValue(client, ZK_PATH);
					String res3 = sendPost("http://10.10.42.107/updateConfig",
							strList3);
					System.out.println("返回结果：" + res3);
					break;
				default:
					break;
				}
			}
		}, pool);
		treeCache.start();
		Thread.sleep(5 * 1000);
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public static String sendPost(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

}
