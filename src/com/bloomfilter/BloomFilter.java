package com.bloomfilter;

import java.util.*;
import java.util.BitSet;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;
/*
 @author cmishra
*/
public class BloomFilter<T> {
	int numRow; // number of items to hold
	float fpProb; // miss prob
	int bitSize; // size of bitset
	int numHash; // number of hash function
	BitSet hashes;
	final String encoding = "UTF-8";
	static MessageDigest digestFunction;
	static final String hashAlgorithm = "MD5"; //
  static {
        try {
            digestFunction = MessageDigest.getInstance(hashAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            System.out.print(e);
        }
    }

	public BloomFilter(int numRow, float fpProb) {
		this.numRow = numRow;
		this.fpProb = fpProb;
		this.bitSize = this.getSize(this.numRow, this.fpProb);
		this.numHash = this.getHashCount(this.numRow, this.bitSize);
		this.hashes = new BitSet(this.bitSize);
	}

	private int getSize(int numRow, float fpProb){
		bitSize = (int)(-(numRow * Math.log(fpProb))/Math.pow(Math.log(2),2));
		return bitSize;
	}

	private int getHashCount(int numRow, int size) {
		numHash = (int) ((size/numRow) * Math.log(2));
		return numHash;
	}

	private long createHash(byte[] data) {
		digestFunction.update(data);
		String encryptedString = new String(digestFunction.digest());
		return encryptedString.hashCode();
    }

	public void add(T item){
		for (int i =0;i< this.numHash;i++) {
			long hash;
			try {
				hash = createHash(item.toString().getBytes(encoding));
			} catch(UnsupportedEncodingException e) {
				hash =0;
			}
			int digest =(int)(Math.abs(hash) % this.bitSize);
			synchronized(this) {
				this.hashes.set(digest);
			}
		}
	}

	public boolean contains(T item){
		for (int i =0;i< this.numHash;i++){
			long hash;
			try {
				hash = createHash(item.toString().getBytes(encoding));

			} catch(UnsupportedEncodingException e) {
				hash =0;
			}
			int digest =(int)(Math.abs(hash) % this.bitSize);
			synchronized(this){
				if (!this.hashes.get(digest))
	        		return false;
				}
		}
		return true;
	}

	public void clear() {
		hashes.clear();
	}

	public int getBloomFilterSize(){
		return hashes.size();
	}
}
