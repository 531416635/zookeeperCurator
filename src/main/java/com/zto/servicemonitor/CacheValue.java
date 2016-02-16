package com.zto.servicemonitor;

import java.util.ArrayList;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

@Component
public class CacheValue {

	@CachePut(value = "books", key = "2")
	public String giveValue(CuratorFramework client, String path)
			throws Exception {
		List<String> children1 = client.getChildren().forPath(path);
		List<String> list1 = new ArrayList<String>();
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
		System.out.println("传入参数：" + strList);
		

		return strList;
	}
}
