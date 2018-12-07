package com.jfinalshop.util;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * Utils - JSON
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
public final class JsonUtils {

	/**
	 * ObjectMapper
	 */
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	/**
	 * 不可实例化
	 */
	private JsonUtils() {
	}

	/**
	 * 将对象转换为JSON字符串
	 * 
	 * @param value
	 *            对象
	 * @return JSON字符串
	 */
	public static String toJson(Object value) {
		Assert.notNull(value);

		try {
			return OBJECT_MAPPER.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 将JSON字符串转换为对象
	 * 
	 * @param json
	 *            JSON字符串
	 * @param valueType
	 *            类型
	 * @return 对象
	 */
	public static <T> T toObject(String json, Class<T> valueType) {
		Assert.hasText(json);
		Assert.notNull(valueType);

		try {
			return OBJECT_MAPPER.readValue(json, valueType);
		} catch (JsonParseException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 将JSON字符串转换为对象
	 * 
	 * @param json
	 *            JSON字符串
	 * @param typeReference
	 *            类型
	 * @return 对象
	 */
	public static <T> T toObject(String json, TypeReference<?> typeReference) {
		Assert.hasText(json);
		Assert.notNull(typeReference);

		try {
			return OBJECT_MAPPER.readValue(json, typeReference);
		} catch (JsonParseException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 将JSON字符串转换为对象
	 * 
	 * @param json
	 *            JSON字符串
	 * @param javaType
	 *            类型
	 * @return 对象
	 */
	public static <T> T toObject(String json, JavaType javaType) {
		Assert.hasText(json);
		Assert.notNull(javaType);

		try {
			return OBJECT_MAPPER.readValue(json, javaType);
		} catch (JsonParseException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 将JSON字符串转换为树
	 * 
	 * @param json
	 *            JSON字符串
	 * @return 树
	 */
	public static JsonNode toTree(String json) {
		Assert.hasText(json);

		try {
			return OBJECT_MAPPER.readTree(json);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 将对象转换为JSON流
	 * 
	 * @param writer
	 *            Writer
	 * @param value
	 *            对象
	 */
	public static void writeValue(Writer writer, Object value) {
		Assert.notNull(writer);
		Assert.notNull(value);

		try {
			OBJECT_MAPPER.writeValue(writer, value);
		} catch (JsonGenerationException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 构造类型
	 * 
	 * @param type
	 *            类型
	 * @return 类型
	 */
	public static JavaType constructType(Type type) {
		Assert.notNull(type);

		return TypeFactory.defaultInstance().constructType(type);
	}

	/**
	 * 构造类型
	 * 
	 * @param typeReference
	 *            类型
	 * @return 类型
	 */
	public static JavaType constructType(TypeReference<?> typeReference) {
		Assert.notNull(typeReference);

		return TypeFactory.defaultInstance().constructType(typeReference);
	}

	/**
	 * 字符串转List<String>
	 * @param jsonStr
	 * @return
	 */
	public static List<String> convertJsonStrToList(String jsonStr) {
		List<String> lists = new ArrayList<String>();
		JSONArray arrays = JSONArray.parseArray(jsonStr);
		if (CollectionUtils.isNotEmpty(arrays)) {
			for(int i = 0; i < arrays.size(); i++) {
				lists.add(arrays.getString(i));
			}
		}
		return lists;
	}

	 /**
     * 将json转化成map
     * @param jsonStr
     * @return
     */
	public static Map<String, String> convertJsonStrToMap(String jsonStr) {
		Map<String, String> map = JSON.parseObject(jsonStr, new com.alibaba.fastjson.TypeReference<Map<String, String>>() { });
		return map;
	}
}