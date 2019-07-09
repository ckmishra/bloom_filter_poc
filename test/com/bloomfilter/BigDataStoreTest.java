package com.bloomfilter;

import java.io.*;
import java.io.FileNotFoundException;
import java.util.Scanner;
import org.json.*;
import com.bloomfilter.BigDataStore;
import java.lang.management.*;

/*
 @author cmishra
*/
public class BigDataStoreTest {
	static BigDataStore bds = new BigDataStore();
	static ThreadMXBean bean = ManagementFactory.getThreadMXBean();

	public static void insert(File file) {
		try {
			long start = bean.getCurrentThreadCpuTime();
			Scanner scanner = new Scanner(file);
			int count=0;
			while (scanner.hasNextLine()) {
				JSONObject obj = new JSONObject(scanner.nextLine());
				bds.insert(obj);
				count++;
			}
			scanner.close();
			long end = bean.getCurrentThreadCpuTime();
			long time = end - start;
			System.out.format("Time taken to Insert all %d the records is %d ns \n", count, time);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void check(File file) {
		try {
			Scanner scanner = new Scanner(file);
			BufferedWriter writer = new BufferedWriter(new FileWriter("./resources/"+file.getName()+"_result.txt"));

			long start = bean.getCurrentThreadCpuTime();
			int count=0;
			while (scanner.hasNextLine()) {
				JSONObject obj = new JSONObject(scanner.nextLine());
				writer.write(bds.check(obj) + "\n");
				count++;
			}
			scanner.close();
			writer.close();
			long end = bean.getCurrentThreadCpuTime();
			long time = end - start;
			System.out.format("Time taken to query all %d the records is %d ns \n", count, time);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		File fileToInsert = new File(args[0]);
		File fileToTest = new File(args[1]);
		insert(fileToInsert); // Preparing the DataStore
		check(fileToTest); // Testing the elements
	}

}
