package com.jfinalshop.api.controller.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Dom4jUtil {
	private static String xmlMsg = "<xml><ToUserName><![CDATA[oXTGiuPU7Jo44I-jVsyGUgho2vpI]]></ToUserName><FromUserName><![CDATA[gh_c2a17bd61548]]></FromUserName><CreateTime><![CDATA[1531155237585]]></CreateTime><MsgType><![CDATA[text]]></MsgType><MsgId><![CDATA[1531155237585]]></MsgId><Content><![CDATA[ 您好呀，很高兴和你成为朋友~沃特权圈圈随时为您服务，您可在对话框内输入以下数字进行解答【1】拆红包享特权【2】精彩权益【3】绑定手机号【4】我的卡券包【5】问题咨询【6】畅越/低消专区]]></Content></xml>";
	//读取一个文档转换为document对象
	public static Document getDocument() {
		Document document = null;
		try {
			SAXReader saxReader = new SAXReader();
			document = saxReader.read(new ByteArrayInputStream(xmlMsg.getBytes("UTF-8")));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return document;
	}
	public static Element getRootElement() {
		Document document = getDocument();
		return document.getRootElement();
	}
	//dom4j转成map
	public static Map<String,String> loopElement(Element ele) {
		Map<String,String> map = new HashMap<String,String>();
		for(Iterator i=ele.elementIterator();i.hasNext();) {
			Element e = (Element)i.next();
			map.put(e.getName(), e.getTextTrim());
		}
		return map;
	}
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
	//xml转对象
	public static Object xmlToBean(String xml,Class<?> clazz) {
		Document document = null;
		Object obj = null;
		SAXReader saxReader = new SAXReader();
		try {
			document= saxReader.read(new ByteArrayInputStream(xml.getBytes("UTF-8")));
			obj = clazz.newInstance();
			Element ele = document.getRootElement();
			List<Element> properties = ele.elements();
			for (Element element : properties) {
				String propertiesName = element.getName();
				String propertiesValue = element.getText();
				Method m = obj.getClass().getMethod("set"+propertiesName, String.class);
				m.invoke(obj, propertiesValue);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	//对象转xml
	public static Document getXmlByBean(Object obj) {
		Document document = DocumentHelper.createDocument();
		try {
			Element root = document.addElement("xml");
//			Element root = document.addElement(obj.getClass().getSimpleName());
			Field[] field = obj.getClass().getDeclaredFields();
			for(int i=0;i<field.length;i++) {
				String name = field[i].getName();
				if (!name.equals("serialVersionUID")) {
					name = name.substring(0, 1).toUpperCase()+name.substring(1);
					Method method = obj.getClass().getMethod("get"+name);
					String propertiesValue = (String)method.invoke(obj);
					Element propertie = root.addElement(name);
					propertie.setText("<![CDATA["+propertiesValue+"]]>");
				}
			}
			Field[] parentField = obj.getClass().getSuperclass().getDeclaredFields();
			for(int i=0;i<parentField.length;i++) {
				String name = parentField[i].getName();
				if (!name.equals("serialVersionUID")) {
					name = name.substring(0, 1).toUpperCase()+name.substring(1);
					Method method = obj.getClass().getMethod("get"+name);
					String propertiesValue = (String)method.invoke(obj);
					Element propertie = root.addElement(name);
					propertie.setText("<![CDATA["+propertiesValue+"]]>");
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return document;
	}
	public static String xmlToString(Document document,String charset,boolean isEcopeStr) {
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding(charset);
		format.setIndentSize(2);
		format.setNewlines(true);
		format.setTrimText(false);
		format.setPadText(true);
		StringWriter sw = new StringWriter();
		XMLWriter xw = new XMLWriter(sw, format);
		xw.setEscapeText(isEcopeStr);
		try {
			xw.write(document);
			xw.flush();
			xw.close();
		} catch (IOException e) {
			System.out.println("格式化XML文档发生异常，请检查！");
			e.printStackTrace();
		}
		return sw.toString();
	}
}

