package com.zto.servicemonitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

@RestController
public class CuratorGet {
	private static final String ZK_ADDRESS = "10.10.19.47:2181";
	private static final String ZK_PATH = "/";
	private static CuratorFramework client;

	/*
	 * @Autowired private CacheRepository cacheRepository;
	 */

	@RequestMapping("/get")
	@Cacheable(value = "books",key="2")
	public String sayResult() throws Exception {
		CuratorFramework client = CuratorFrameworkFactory.newClient(ZK_ADDRESS,
				new RetryNTimes(10, 5000));
		client.start();
		List<String> children1 = client.getChildren().forPath("/");
		List<String> list1 = new ArrayList<String>();
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		for (String path1 : children1) {
			String data1 = new String(client.getData().forPath("/" + path1),
					"GB2312");
			List<String> children2 = client.getChildren().forPath("/" + path1);
			List<String> list2 = new ArrayList<String>();
			if (data1 != "") {
				for (String path2 : children2) {
					String data2 = new String(client.getData().forPath(
							"/" + path1 + "/" + path2), "GB2312");
					list2.add(path2 + "/" + data2);
				}
				String jsonArr1 = JSON.toJSONString(list2)
						.replace("\",\"", ",").replace("[", "")
						.replace("]", "");
				if (jsonArr1.equals("")) {
					list1.add("{\"source\":\"/" + path1 + "\",\"target\":"
							+ "\"\"" + "}");
				} else {
					list1.add("{\"source\":\"/" + path1 + "\",\"target\":"
							+ jsonArr1 + "}");
				}
			}
		}
		String strList = "";
		if (list1.size() > 0) {
			strList += list1.toString();
		}
		System.out.println("缓存测试");
		return strList;
	}

}
