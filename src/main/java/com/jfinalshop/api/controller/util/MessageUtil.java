package com.jfinalshop.api.controller.util;//package com.util;
//
//
//import java.io.InputStream;
//import java.io.Writer;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.log4j.Logger;
//import org.dom4j.Document;
//import org.dom4j.Element;
//import org.dom4j.io.SAXReader;
//
//import com.bean.messagebean.article.Articles;
//import com.bean.messagebean.article.ArticlesMessage;
//import com.bean.messagebean.image.ImageMessage;
//import com.bean.messagebean.music.MusicMessage;
//import com.bean.messagebean.text.TextMessage;
//import com.bean.messagebean.video.Video;
//import com.bean.messagebean.video.VideoMessage;
//import com.bean.messagebean.voice.VoiceMessage;
//import com.thoughtworks.xstream.XStream;
//import com.thoughtworks.xstream.core.util.QuickWriter;
//import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
//import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
//import com.thoughtworks.xstream.io.xml.XppDriver;
//
///**
// * 消息处理工具类
// *
// */
//public class MessageUtil {
//
//	public static String TAG = "MessageUtil";
//
//	private static Logger logger = Logger.getLogger(MessageUtil.class);
//
//	// 请求消息类型：文本
//	public static final String MESSAGE_TYPE_TEXT = "text";
//
//	/**
//	 * 解析微信发来的请求（XML）
//	 *
//	 * @param
//	 * @return Map<String, String>
//	 * @throws Exception
//	 */
//	@SuppressWarnings("unchecked")
//	public static Map<String, String> parseXml(InputStream inputStream) throws Exception {
//		// 将解析结果存储在HashMap中
//		Map<String, String> map = new HashMap<String, String>();
//		logger.debug(TAG + " begin");
//		// 从request中取得输入流
//		// InputStream inputStream = request.getInputStream();
//		// 读取输入流
//		SAXReader reader = new SAXReader();
//		Document document = reader.read(inputStream);
//		logger.debug(TAG + " read inputStream");
//		// 得到xml根元素
//		Element root = document.getRootElement();
//		// 得到根元素的所有子节点
//		List<Element> elementList = root.elements();
//		// 遍历所有子节点
//		for (Element e : elementList) {
//			map.put(e.getName(), e.getText());
//			logger.debug(TAG + " ###### log4j debug" + e.getName() + " : " + e.getText());
//		}
//
//		// 释放资源
//		inputStream.close();
//		inputStream = null;
//
//		return map;
//	}
//
//	/**
//	 * 扩展xstream使其支持CDATA
//	 */
//	private static XStream xstream = new XStream(new XppDriver() {
//		public HierarchicalStreamWriter createWriter(Writer out) {
//			return new PrettyPrintWriter(out) {
//				// 对所有xml节点的转换都增加CDATA标记
//				boolean cdata = true;
//
//				@SuppressWarnings("rawtypes")
//				public void startNode(String name, Class clazz) {
//					super.startNode(name, clazz);
//				}
//
//				protected void writeText(QuickWriter writer, String text) {
//					if (cdata) {
//						writer.write("<![CDATA[");
//						writer.write(text);
//						writer.write("]]>");
//					} else {
//						writer.write(text);
//					}
//				}
//			};
//		}
//	});
//
//	/**
//	 * 文本消息对象转换成xml
//	 *
//	 * @param textMessage
//	 *            文本消息对象
//	 * @return xml
//	 */
//	public static String messageToXml(TextMessage textMessage) {
//		xstream.alias("xml", textMessage.getClass());
//		return xstream.toXML(textMessage);
//	}
//	/**
//	 * 图片消息对象转换成xml
//	 *
//	 * @param imageMessage
//	 *            图片消息对象
//	 * @return xml
//	 */
//	public static String messageToXml(ImageMessage imageMessage) {
//		xstream.alias("xml", imageMessage.getClass());
//		return xstream.toXML(imageMessage);
//	}
//	/**
//	 * 音乐消息对象转换成xml
//	 *
//	 * @param musicMessage
//	 *            音乐消息对象
//	 * @return xml
//	 */
//	public static String messageToXml(MusicMessage musicMessage) {
//		xstream.alias("xml", musicMessage.getClass());
//		return xstream.toXML(musicMessage);
//	}
//	/**
//	 * 音频消息对象转换成xml
//	 *
//	 * @param videoMessage
//	 *            音频消息对象
//	 * @return xml
//	 */
//	public static String messageToXml(VideoMessage videoMessage) {
//		xstream.alias("xml", videoMessage.getClass());
//		return xstream.toXML(videoMessage);
//	}
//	/**
//	 * 视频消息对象转换成xml
//	 *
//	 * @param voiceMessage
//	 *            视频消息对象
//	 * @return xml
//	 */
//	public static String messageToXml(VoiceMessage voiceMessage) {
//		xstream.alias("xml", voiceMessage.getClass());
//		return xstream.toXML(voiceMessage);
//	}
//	/**
//	 * 多图文消息对象转换成xml
//	 *
//	 * @param articlesMessage
//	 *            多图文消息对象
//	 * @return xml
//	 */
//	public static String messageToXml(ArticlesMessage articlesMessage) {
//
//		xstream.alias("item", Articles.class);
//		xstream.alias("xml", articlesMessage.getClass());
//		return xstream.toXML(articlesMessage);
//	}
//
//}
