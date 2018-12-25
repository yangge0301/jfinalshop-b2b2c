package com.jfinalshop.api.controller.util;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MessageDomUtil {

	public static final String MESSAGE_TYPE_TEXT = "text";
    /**
     * 新建方法，将接收到的XML格式，转化为Map对象
     *
     * @param is 将request对象，通过参数传入
     * @return 返回转换后的Map对象
     */
    public static Map<String, String> xmlToMap(InputStream is) throws IOException, DocumentException {
        Map<String, String> map = new HashMap<String, String>();
        SAXReader reader = new SAXReader();
        Document doc = reader.read(is);//从reader对象中,读取输入流
        Element root = doc.getRootElement();//获取XML文档的根元素
        List<Element> list = root.elements();//获得根元素下的所有子节点
        for (Element e : list) {
            map.put(e.getName(), e.getText());//遍历list对象，并将结果保存到集合中
        }
        is.close();
        return map;
    }

}