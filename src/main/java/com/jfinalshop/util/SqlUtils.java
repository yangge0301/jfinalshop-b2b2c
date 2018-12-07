package com.jfinalshop.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class SqlUtils {

	/**
	 * 拼接sql in语句
	 * 
	 * @param ids
	 * @param count
	 * @param field
	 * @return
	 */
	public static String getSQLIn(List<?> ids) {
		int count = 50;
		int len = ids.size();
		int size = len % count;
		if (size == 0) {
			size = len / count;
		} else {
			size = (len / count) + 1;
		}
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < size; i++) {
			int fromIndex = i * count;
			int toIndex = Math.min(fromIndex + count, len);
			String productId = StringUtils.defaultIfEmpty(StringUtils.join(ids.subList(fromIndex, toIndex), "','"), "");
//			if (i != 0) {
//				builder.append(" OR ");
//			}
			builder.append(" ('").append(productId) .append("') ");
		}
		return StringUtils.defaultIfEmpty(builder.toString(), " ('') ");
	}
	
	/**
	 * 将 id 列表 join 起来，用逗号分隔，并且用小括号括起来
	 */
	public static void joinIds(List<Integer> idList, StringBuilder ret) {
		ret.append("(");
		boolean isFirst = true;
		for (Integer id : idList) {
			if (isFirst) {
				isFirst = false;
			} else {
				ret.append(", ");
			}
			ret.append(id.toString());
		}
		ret.append(")");
	}
}
