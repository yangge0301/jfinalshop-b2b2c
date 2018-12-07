package com.jfinalshop.util.sequence;

/**
 * 基于Twitter的Snowflake算法实现分布式高效有序ID生产黑科技(sequence)
 * 
 */
public class UidGenerator {

	static Sequence sequence = new Sequence(0, 0);
	
	public static long nextId() {
		long id = sequence.nextId();
		return id;
	}
	
}
