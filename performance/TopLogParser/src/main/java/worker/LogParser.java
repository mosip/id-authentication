package worker;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.ProcessResUsage;
import models.SystemCPU;
import models.SystemMemory;

public class LogParser {

	private String FILE_PATH = "C:\\Sample Logs\\TOP Log\\top-output_idrepo_12Mar_20users.txt";

	private String BASE_PATH = "C:\\Sample Logs\\TOP Log\\12March";

	private List<SystemCPU> cpuData;
	private List<SystemMemory> memoryData;
	private List<ProcessResUsage> processData;

	public LogParser() {

	}

	public void parseTopLog(String logFile) {

		ProcessResUsage processUsage = null;
		SystemCPU systemCPU = null;
		SystemMemory systemMemory = null;
		processData = new ArrayList<ProcessResUsage>();
		cpuData = new ArrayList<SystemCPU>();
		memoryData = new ArrayList<SystemMemory>();
		BufferedReader reader;

		String systemDataRegex = "^\\s*%Cpu\\(s\\)\\s*:\\s*([\\d\\.]+)\\s+us,\\s+([\\d\\.]+)\\s+sy,.*";
		String systemMemRegex = "^\\s*KiB\\s*\\s+Mem\\s+:\\s+([\\d]+)\\s+total,\\s+([\\d]+)\\s+free,\\s+([\\d]+)\\s+used,\\s+([\\d]+)\\s+buff.*$";

		String processDataRegex = "^\\s*\\d+\\s\\w+\\s+.*(\\d{7,10})\\s+[\\d\\.g]+.*[A-Z]{1}\\s+([\\d\\.]+)\\s+([\\d\\.]+)\\s+.*java";
		processDataRegex = "^\\s*\\d+\\s\\w+\\s+.*(\\d{7,10})\\s+([\\d\\.]+)g.*[A-Z]{1}\\s+([\\d\\.]+)\\s+([\\d\\.]+)\\s+.*java";
		processDataRegex = "^\\s*\\d+\\s+\\w+\\s+\\d+\\s+\\d\\s+([\\d\\.]+)g\\s+([\\d\\.]+)g\\s+.*([\\d\\.]+)\\s+([\\d\\.]+)\\s+.*java";
		String timePattern = "^\\s*top\\s-\\s(\\d{1,2}:\\d{1,2}:\\d{1,2})\\sup.*";
		timePattern = "^(top).*$";
		final Pattern p = Pattern.compile(timePattern);
		try {
			reader = new BufferedReader(new FileReader(FILE_PATH));
			String line = reader.readLine();
			int counter = 1;
			while (line != null) {

				boolean lineHasKibMem = line.contains("KiB Mem");
				boolean lineHasTop = line.contains("load average");
				boolean lineHasCpus = line.contains("%Cpu(s)");
				boolean lineHasJava = line.contains("java");
				// System.out.println("\nHey!");
				if (lineHasTop) {
					// System.out.println(line);
					systemCPU = new SystemCPU();
					processUsage = new ProcessResUsage();
					systemMemory = new SystemMemory();
					String[] list = line.split(" ");
					String time = list[2];
					// System.out.print(time);
					systemCPU.setTime(time);
					systemMemory.setTime(time);
					// System.out.println("Tme from inside the object:");
					// System.out.println(systemCPU.getTime());
					processUsage.setTime(time);
				} else if (lineHasCpus) {

					Pattern pattern = Pattern.compile(systemDataRegex);
					Matcher matcher = pattern.matcher(line);
					boolean found = matcher.find();

					if (found) {
						String user = matcher.group(1);
						String system = matcher.group(2);
						systemCPU.setSystem(new Float(system));
						systemCPU.setUser(new Float(user));
						// System.out.print(" " + user + " " + system);
						cpuData.add(systemCPU);
					}

					// break;
				} else if (lineHasKibMem) {

					Pattern pattern = Pattern.compile(systemMemRegex);
					Matcher matcher = pattern.matcher(line);
					boolean found = matcher.find();
					if (found) {
						systemMemory.setTotal(new Float(matcher.group(1)) / 1024);
						systemMemory.setFree(new Float(matcher.group(2)) / 1024);
						systemMemory.setUsed(new Float(matcher.group(3)) / 1024);
						systemMemory.setBuffer(new Float(matcher.group(4)) / 1024);
						memoryData.add(systemMemory);
					}

				} else if (lineHasJava) {
					Pattern pattern = Pattern.compile(processDataRegex);
					Matcher matcher = pattern.matcher(line);
					boolean found = matcher.find();
					if (found) {
						// System.out.println("matched");
						String virt = matcher.group(1);
						String res = matcher.group(2);
						String cpuPer = matcher.group(3);
						String memPer = matcher.group(4);
						processUsage.setVirtualSpace(new Float(virt));
						processUsage.setRes(new Float(res));
						processUsage.setCpuUsage(new Float(cpuPer));
						processUsage.setMemoryUsage(new Float(memPer));
						// System.out.print(" " + virt + " " + cpuPer + " " + memPer);
						processData.add(processUsage);
					}

					// System.out.println(" " + "index" + counter);
					// if (counter == 980)
					// break;
					counter++;
				}
				line = reader.readLine();

			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

//		for (SystemCPU s : cpuData) {
//			System.out.println(s.getTime());
//			System.out.println(s.getUser());
//			System.out.println(s.getSystem());
//		}

	}

	public void processParsedLogData() {

//		cpuData.size();
//		memoryData.size();
//		processData.size();

// 		System.out.println("System level CPU data");
//		for (SystemCPU s : cpuData) {
//			System.out.print("\n" + s.getTime());
//			System.out.print(" " + s.getUser());
//			System.out.print(" " + s.getSystem());
//		}

//		System.out.println("System level memory Data:");
//		System.out.println();
//		int i = 01;
//		for (SystemMemory m : memoryData) {
//
//			System.out.println(m.getTime() + ", total: " + m.getTotal() + ", free: " + m.getFree() + ", used: "
//					+ m.getUsed() + " ,index" + i++);
//
//		}

		// System.out.println("Process level data");

//		for (ProcessResUsage s : processData) {
//
//			System.out.print(s.getTime() + " ");
//			System.out.print(s.getVirtualSpace() + " ");
//			System.out.print(s.getCpuUsage() + " ");
//			System.out.println(s.getMemoryUsage() + " ");
//
//		}

//		System.out.println("\nSize of list having process data: " + processData.size());
//		System.out.println("Size of list having CPU data: " + cpuData.size());
//		System.out.println("Size of list having CPU data: " + memoryData.size());

		Object[][] cpuDataSet = new Object[cpuData.size() + 1][3];

		Object[] header = { "time", "user", "system" };
		cpuDataSet[0] = header;
		Object[] data = new Object[3];
		int index = 1;
		for (SystemCPU s : cpuData) {
			data = new Object[3];
			data[0] = s.getTime();
			data[1] = s.getUser();
			data[2] = s.getSystem();
			cpuDataSet[index++] = data;
		}

		// System.out.println(cpuDataSet.length);
		String filepath = BASE_PATH + File.separator + "top-output_cpu.xlsx";

		String sheetName = "System level CPU";
		Util.writeToExcel(cpuDataSet, filepath, sheetName);
		// Util.writeToExistingExcel(cpuDataSet, filepath, sheetName);

		Object[][] processDataSet = new Object[processData.size() + 1][5];
		Object[] header1 = { "time", "virtual space(GB)", "res(GB)", "cpu(%)", "memory(%)" };
		processDataSet[0] = header1;
		index = 1;
		Object[] data1 = new Object[5];
		// System.out.println("Size of data1 array:-");
		// System.out.println(data1.length);
		for (ProcessResUsage s : processData) {
			data1 = new Object[5];
			data1[0] = s.getTime();
			data1[1] = s.getVirtualSpace();
			data1[2] = s.getRes();
			data1[3] = s.getCpuUsage();
			data1[4] = s.getMemoryUsage();

			processDataSet[index++] = data1;
		}

		String filepath1 = BASE_PATH + File.separator + "top-output_process.xlsx";
		String sheetName1 = "process level";
		Util.writeToExcel(processDataSet, filepath1, sheetName1);
		// Util.writeToExistingExcel(processDataSet, filepath, sheetName1);

		Object[][] memoryDataSet = new Object[memoryData.size() + 1][5];
		Object[] header2 = { "time", "total(MB)", "free(MB)", "used(MB)", "buff(MB)" };
		memoryDataSet[0] = header2;
		index = 1;
		data1 = new Object[5];
		for (SystemMemory s : memoryData) {
			data1 = new Object[5];
			data1[0] = s.getTime();
			data1[1] = s.getTotal();
			data1[2] = s.getFree();
			data1[3] = s.getUsed();
			data1[4] = s.getBuffer();
			memoryDataSet[index++] = data1;
		}
		sheetName = "System Memory";
		filepath = BASE_PATH + File.separator + "top-output_memory.xlsx";
		Util.writeToExcel(memoryDataSet, filepath, sheetName);
		// Util.writeToExistingExcel(memoryDataSet, filepath, sheetName);

	}

}
