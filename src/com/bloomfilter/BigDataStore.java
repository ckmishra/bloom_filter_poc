package com.bloomfilter;

import java.util.*;
import java.lang.*;
import java.io.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.json.*;
import com.bloomfilter.BloomFilter;

/*
 @author cmishra

*/
enum RESPONSE {
	YES("Yes"), NO("No"), INVALID_QUERY("Invalid Query");
	private final String response;
  private RESPONSE(String response) {
      this.response = response;
  }
	public String toString() {
        return this.response;
    }
}

class BigDataStore implements Serializable, Cloneable {
	private BloomFilter bf_user;
	private BloomFilter bf_merc;
	private BloomFilter bf_day;
	private float fp_prob = 0.01f;
	private final Lock lock = new ReentrantLock();

	public BigDataStore() {
		bf_user = new BloomFilter(50000000, fp_prob); //user cardinality is 50 Million
		bf_merc = new BloomFilter(10000, fp_prob);//user cardinality is 10 Thousands
		bf_day = new BloomFilter(1830,fp_prob); // last 5 years
		System.out.format("BitSet Size of user bf is %d \n", bf_user.getBloomFilterSize());
		System.out.format("BitSet Size of merchant bf is %d \n", bf_merc.getBloomFilterSize());
		System.out.format("BitSet Size of day bf is %d \n", bf_day.getBloomFilterSize());
	}

	public void insert(JSONObject record) {
		lock.lock(); // aquire a lock, to achieve montonic operation accross three filters
		try {
			if(!isValidQuery(record)) return;

			bf_user.add(record.getString("user_id")); // add to user bloom filter
			bf_merc.add(record.getString("merchant_id")); // add to merchant bloom filter
			bf_day.add(record.getString("day"));	// add to day bloom filter
		} catch(Exception e){
			 System.out.print(e);
		} finally {
			lock.unlock(); // free a lock
		}
	}

	private boolean isValidQuery(JSONObject record) {
		if(record == null && record.isNull("user_id") && record.isNull("merchant_id") && record.isNull("day"))
			return false;
		return true;
	}

	public synchronized String check(JSONObject query) {
		try {
			boolean is_user_available = false;
			boolean is_mer_available = false;
			boolean is_day_available = false;
			query = (JSONObject)query.get("query");

			if(!isValidQuery(query))
					return (RESPONSE.INVALID_QUERY.toString());;

			if(!query.isNull("day")){
				if(bf_day.contains(query.getString("day")))
					is_day_available = true;
				else
					return (RESPONSE.YES.toString());
			}
			if(!query.isNull("merchant_id")){
				if(bf_merc.contains(query.getString("merchant_id")))
					is_mer_available =true;
				else
					return (RESPONSE.YES.toString());
			}
			if(!query.isNull("user_id")){
				if(bf_user.contains(query.getString("user_id")))
					is_user_available =true;
				else
					return (RESPONSE.YES.toString());
			}

			if(is_day_available || is_mer_available || is_user_available)
				return (RESPONSE.NO.toString());
		} catch(Exception e){
			System.out.print(e);
		}
		return (RESPONSE.YES.toString());
 	}
}
