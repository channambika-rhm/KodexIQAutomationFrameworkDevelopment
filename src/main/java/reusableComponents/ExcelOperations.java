package reusableComponents;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelOperations {
	
	String filePath;
	Sheet sh;
	public ExcelOperations(String excelFile, String sheetName) {
			
		 try {

			filePath = System.getProperty("user.dir")+excelFile;
		} catch (Exception e) {
			e.printStackTrace();
		}
		 	//open file - workbook
			File testDataFile = new File(filePath);
			Workbook wb = null;
			try {
				//factory for creating the appropriate kind of workbook(be HSSFWORKBOOK OR XSSFWORKBOOK), by autodetecting
				// from the supplied input
				wb = WorkbookFactory.create(testDataFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			 sh = wb.getSheet(sheetName);
	}
	
	//get test data from  excel sheet in hashmap based on row number
	public HashMap<String, String> getTestDataInMap(int rowNum) throws Exception {
				//read data row by row and put in map
		HashMap<String, String> hm = new HashMap<String, String>();
		for (int i = 0; i < sh.getRow(0).getLastCellNum(); i++) {
			sh.getRow(rowNum).getCell(i).setCellType(CellType.STRING);
			//System.out.println(sh.getRow(0).getCell(i).toString());
			hm.put(sh.getRow(0).getCell(i).toString(), sh.getRow(rowNum).getCell(i).toString());
			//System.out.println("from excel getting this data");
		//System.out.println(hm);
		}	
		return hm;
	}
	
	//get row count
	public int getRowCount() {		
		return sh.getLastRowNum();
	}
	
	//get column count
	public int getColCount() {
		return sh.getRow(0).getLastCellNum();
		
	}
}
