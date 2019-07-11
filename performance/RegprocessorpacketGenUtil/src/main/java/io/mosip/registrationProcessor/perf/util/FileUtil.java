package io.mosip.registrationProcessor.perf.util;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.mosip.registrationProcessor.perf.dto.RegPacketSyncDto;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {

	public static List<String> readLinesOfFile(String filePath) throws IOException {
		List<String> result = new ArrayList<>();
		try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
			result = lines.collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void logSyncDataToFile(RegPacketSyncDto syncDto, String filePath) {
		try (FileWriter f = new FileWriter(filePath, true);
				BufferedWriter b = new BufferedWriter(f);
				PrintWriter p = new PrintWriter(b);) {

			p.println(syncDto.getRegId() + "," + syncDto.getSyncData() + "," + syncDto.getPacketPath() + ","
					+ syncDto.getReferenceId());

		} catch (IOException i) {
			i.printStackTrace();
		}
	}

}
