
package com.emotibot.jsEngine.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
/**
 * 允许多个读操作同时进行，但每次只允许一个写操作
 * @author liguorui
 * @param <K>
 * @param <V>
 */
public class ReadWriteMap<K, V> {
	private enum OPERA {PUT,PUTALL,DELTE,CLEAR}  
	private final Map<K, V> map = new HashMap<K, V>();
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock r = lock.readLock();
	private final Lock w = lock.writeLock();
	public V put(K key, V value) {
		return this.operate(key, value, OPERA.PUT);
	}
	public void remove(K key){
		this.operate(key,null,OPERA.DELTE);
	}
	public void clear(){
		this.operate(null,null,OPERA.CLEAR);
	}
	public void putAll(Map<K, V> map){
		this.operate(null,null,OPERA.PUTALL);
	}
	public V operate(K key, V value,OPERA opera){
		w.lock();
		System.out.println("获取写锁");
		try {
			if(opera.equals(OPERA.PUT.toString())){
				return map.put(key, value);
			}
			else if(opera.equals(OPERA.DELTE.toString())){
				return map.remove(key);
			}
			else if(opera.equals(OPERA.PUTALL.toString())){
				map.clear();
				map.putAll(map);
				return null;
			}
			else if(opera.equals(OPERA.CLEAR.toString())){
				map.clear();
				return null;
			}
			else{
				return null;
			}
		} finally {
			w.unlock();
			System.out.println("释放写锁");
		}
	}
	public V get(K key) {
		r.lock();
		System.out.println("获取读锁");
		try {
			return map.get(key);
		} finally  {
			r.unlock();
			System.out.println("释放读锁");
		}
	}
}