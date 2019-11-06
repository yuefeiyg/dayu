package com.dayu.util;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExcelUtil {

    public static void exportExcel(){

        HSSFWorkbook workbook=new HSSFWorkbook();

        HSSFSheet sheet=workbook.createSheet("sheet2");



    }
}
