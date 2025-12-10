package reusableComponents;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * Enhanced ExcelOperations class with: ✅ Auto Backup & Silent Restore ✅ Backup
 * Retention (7 days) + Max 5 Backups ✅ Compact File Logging ✅ Thread-safe
 * Writes ✅ Bulk Write Support with Auto Timestamp
 */
public class ExcelOperations {

	private final String filePath;
	private final String sheetName;
	private static final String BACKUP_DIR = System.getProperty("user.dir") + "/backups/";
	private static final String LOG_DIR = System.getProperty("user.dir") + "/logs/";
	private static final String LOG_FILE = LOG_DIR + "ExcelOps.log";
	private static final int MAX_BACKUPS = 5;
	private static final int BACKUP_RETENTION_DAYS = 7;
 
	public ExcelOperations(String excelFile, String sheetName) {
		if (excelFile == null || excelFile.trim().isEmpty()) {
			throw new IllegalArgumentException("Excel file path must not be null/empty");
		}
		this.filePath = resolvePath(excelFile);
		this.sheetName = sheetName;
		initDirectories();
		checkAndRestoreIfCorrupted();
	}

	private String resolvePath(String path) {
		File f = new File(path);
		if (!f.isAbsolute()) {
			// allow leading slash in config, remove leading slashes then append to user.dir
			String cleaned = path.replaceFirst("^/+", "");
			f = new File(System.getProperty("user.dir"), cleaned);
		}
		return f.getAbsolutePath();
	}

	/** Initialize required directories */
	private void initDirectories() {
		new File(BACKUP_DIR).mkdirs();
		new File(LOG_DIR).mkdirs();
	}

	/** Write compact log entries */
	private synchronized void log(String level, String message) {
		try (FileWriter fw = new FileWriter(LOG_FILE, true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {

			String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			out.println(timestamp + " " + level + " " + message);

		} catch (IOException e) {
			System.out.println("⚠️ Failed to write log: " + e.getMessage());
		}
	}

	/** Check if Excel file is corrupted and restore silently */
	private void checkAndRestoreIfCorrupted() {
		try (FileInputStream fis = new FileInputStream(filePath)) {
			WorkbookFactory.create(fis).close();
		} catch (Exception e) {
			log("WARN", "Excel file corrupted → attempting restore");
			restoreLatestBackup();
		}
	}

	/** Open workbook safely */
	private Workbook openWorkbook() throws Exception {
		try (FileInputStream fis = new FileInputStream(filePath)) {
			return WorkbookFactory.create(fis);
		}
	}

	/** Create automatic backup before modification */
	private synchronized void createBackup() {
		try {
			File original = new File(filePath);
			if (!original.exists())
				return;

			String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			String backupFile = BACKUP_DIR + "Backup_" + timestamp + "_" + new File(filePath).getName() + ".bak";
			Files.copy(original.toPath(), new File(backupFile).toPath());
			log("INFO", "Backup created: " + backupFile);

			cleanupOldBackups();

		} catch (Exception e) {
			log("ERROR", "Failed to create backup: " + e.getMessage());
		}
	}

	/** Restore the latest valid backup silently */
	private synchronized void restoreLatestBackup() {
		try {
			File backupFolder = new File(BACKUP_DIR);
			File[] backups = backupFolder.listFiles((dir, name) -> name.endsWith(".bak"));

			if (backups == null || backups.length == 0) {
				log("ERROR", "No backup files found to restore");
				return;
			}

			Arrays.sort(backups, Comparator.comparingLong(File::lastModified).reversed());
			File latestBackup = backups[0];

			Files.copy(latestBackup.toPath(), new File(filePath).toPath(),
					java.nio.file.StandardCopyOption.REPLACE_EXISTING);
			log("INFO", "Excel auto-restored from backup: " + latestBackup.getName());

		} catch (Exception e) {
			log("ERROR", "Failed to restore from backup: " + e.getMessage());
		}
	}

	/** Clean up old backups by age and count */
	private synchronized void cleanupOldBackups() {
		try {
			File backupFolder = new File(BACKUP_DIR);
			File[] backups = backupFolder.listFiles((dir, name) -> name.endsWith(".bak"));
			if (backups == null || backups.length == 0)
				return;

			long now = System.currentTimeMillis();
			List<File> validBackups = new ArrayList<>();

			for (File backup : backups) {
				long diffDays = (now - backup.lastModified()) / (1000 * 60 * 60 * 24);
				if (diffDays > BACKUP_RETENTION_DAYS) {
					backup.delete();
					log("INFO", "Deleted expired backup: " + backup.getName());
				} else {
					validBackups.add(backup);
				}
			}

			validBackups = validBackups.stream().sorted(Comparator.comparingLong(File::lastModified).reversed())
					.collect(Collectors.toList());

			for (int i = MAX_BACKUPS; i < validBackups.size(); i++) {
				File oldBackup = validBackups.get(i);
				oldBackup.delete();
				log("INFO", "Deleted old backup (exceeded limit): " + oldBackup.getName());
			}

		} catch (Exception e) {
			log("ERROR", "Failed to clean up backups: " + e.getMessage());
		}
	}

	/** Read test data into a map */
	public HashMap<String, String> getTestDataInMap(int rowNum) throws Exception {
		HashMap<String, String> hm = new HashMap<>();

		try (Workbook wb = openWorkbook()) {
			Sheet sh = wb.getSheet(sheetName);
			Row headerRow = sh.getRow(0);
			Row dataRow = sh.getRow(rowNum);

			for (int i = 0; i < headerRow.getLastCellNum(); i++) {
				Cell headerCell = headerRow.getCell(i);
				String headerName = (headerCell == null) ? "" : headerCell.toString().trim();

				Cell dataCell = (dataRow == null) ? null : dataRow.getCell(i);
				if (dataCell == null && dataRow != null) {
					dataCell = dataRow.createCell(i);
					dataCell.setCellValue("");
				}

				if (dataCell != null) {
					dataCell.setCellType(CellType.STRING);
					hm.put(headerName, dataCell.getStringCellValue().trim());
				}
			}
		}
		return hm;
	}

	public int getRowCount() throws Exception {
		try (Workbook wb = openWorkbook()) {
			return wb.getSheet(sheetName).getLastRowNum();
		}
	}

	/** Write individual result/status/time */
	public synchronized void writeResult(int rowNum, String status) {
		writeToColumn(rowNum, "Status", status, true);
	}

	public synchronized void writeActualResult(int rowNum, String actualResult) {
		writeToColumn(rowNum, "ActualResult", actualResult, false);
	}

	public synchronized void writeExecutionTime(int rowNum) {
		String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		writeToColumn(rowNum, "ExecutionTime", timestamp, false);
	}

	/** Internal write method */
	private synchronized void writeToColumn(int rowNum, String columnName, String value, boolean applyColor) {
		Workbook wb = null;
		FileOutputStream fos = null;

		try {
			createBackup();

			try (FileInputStream fis = new FileInputStream(filePath)) {
				wb = WorkbookFactory.create(fis);
			}

			Sheet sh = wb.getSheet(sheetName);
			Row headerRow = sh.getRow(0);
			int colIndex = -1;

			for (int i = 0; i < headerRow.getLastCellNum(); i++) {
				if (headerRow.getCell(i).getStringCellValue().equalsIgnoreCase(columnName)) {
					colIndex = i;
					break;
				}
			}

			if (colIndex == -1) {
				colIndex = headerRow.getLastCellNum();
				Cell newHeader = headerRow.createCell(colIndex);
				newHeader.setCellValue(columnName);
			}

			Row dataRow = sh.getRow(rowNum);
			if (dataRow == null)
				dataRow = sh.createRow(rowNum);

			Cell cell = dataRow.getCell(colIndex);
			if (cell == null)
				cell = dataRow.createCell(colIndex);
			cell.setCellValue(value);

			if (applyColor && columnName.equalsIgnoreCase("Status")) {
				CellStyle style = wb.createCellStyle();
				style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				if (value.equalsIgnoreCase("PASS")) {
					style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
				} else if (value.equalsIgnoreCase("FAIL")) {
					style.setFillForegroundColor(IndexedColors.RED.getIndex());
				} else {
					style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
				}
				Font font = wb.createFont();
				font.setColor(IndexedColors.BLACK.getIndex());
				style.setFont(font);
				cell.setCellStyle(style);
			}

			fos = new FileOutputStream(filePath);
			wb.write(fos);

			log("INFO", "Excel Updated → Row " + rowNum + ", Column: " + columnName + " = " + value);
			System.out.println("📘 Excel Updated → Row " + rowNum + ", Column: " + columnName + " = " + value);

		} catch (Exception e) {
			log("ERROR", "Failed to update Excel (Row " + rowNum + ", Column: " + columnName + "): " + e.getMessage());
		} finally {
			try {
				if (fos != null)
					fos.close();
				if (wb != null)
					wb.close();
			} catch (Exception ignored) {
			}
		}
	}

	/** Bulk write multiple columns with auto timestamp */
	public synchronized void writeResultSet(int rowNum, Map<String, String> dataMap) {
		Workbook wb = null;
		FileOutputStream fos = null;

		try {
			createBackup();

			try (FileInputStream fis = new FileInputStream(filePath)) {
				wb = WorkbookFactory.create(fis);
			}

			Sheet sh = wb.getSheet(sheetName);
			Row headerRow = sh.getRow(0);
			if (headerRow == null)
				headerRow = sh.createRow(0);

			Row dataRow = sh.getRow(rowNum);
			if (dataRow == null)
				dataRow = sh.createRow(rowNum);

			// Auto timestamp
			String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			dataMap.put("ExecutionTime", timestamp);

			for (Map.Entry<String, String> entry : dataMap.entrySet()) {
				String columnName = entry.getKey();
				String value = entry.getValue();

				int colIndex = -1;
				for (int i = 0; i < headerRow.getLastCellNum(); i++) {
					Cell headerCell = headerRow.getCell(i);
					if (headerCell != null && headerCell.getStringCellValue().equalsIgnoreCase(columnName)) {
						colIndex = i;
						break;
					}
				}

				if (colIndex == -1) {
					colIndex = headerRow.getLastCellNum() == -1 ? 0 : headerRow.getLastCellNum();
					Cell newHeader = headerRow.createCell(colIndex);
					newHeader.setCellValue(columnName);
				}

				Cell cell = dataRow.getCell(colIndex);
				if (cell == null)
					cell = dataRow.createCell(colIndex);
				cell.setCellValue(value);

				// Color if status
				if (columnName.equalsIgnoreCase("Status")) {
					CellStyle style = wb.createCellStyle();
					style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					if ("PASS".equalsIgnoreCase(value)) {
						style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
					} else if ("FAIL".equalsIgnoreCase(value)) {
						style.setFillForegroundColor(IndexedColors.RED.getIndex());
					} else {
						style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
					}
					Font font = wb.createFont();
					font.setColor(IndexedColors.BLACK.getIndex());
					style.setFont(font);
					cell.setCellStyle(style);
				}
			}

			fos = new FileOutputStream(filePath);
			wb.write(fos);

			String joined = dataMap.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue())
					.collect(Collectors.joining(", "));

			log("INFO", "Excel Updated → Row " + rowNum + ", Columns: " + joined);
			System.out.println("📘 Excel Updated → Row " + rowNum + ", Columns: " + joined);

		} catch (Exception e) {
			log("ERROR", "Failed to update Excel Row " + rowNum + ": " + e.getMessage());
		} finally {
			try {
				if (fos != null)
					fos.close();
				if (wb != null)
					wb.close();
			} catch (Exception ignored) {
			}
		}
	}
}