package poi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHeight;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPr;
import org.w3c.dom.Node;

/**
 * 使用POI,进行Word相关的操作
 *
 *
 * @author    xuyu
 *
 * <p>Modification History:</p>
 * <p>Date       Author      Description</p>
 * <p>------------------------------------------------------------------</p>
 * <p> </p>
 * <p>  </p>
 */
public class MSWordTool {


	//这鬼东西会替换单元格原有的
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		MSWordTool changer = new MSWordTool();
		changer.setTemplate("/media/zhangjun/新加卷/Word.docx");
		Map<String,String> content = new HashMap<String,String>();
		content.put("adr", "格式规范、标准统一、利于阅览");
		content.put("aaa", "格式规范、标准统一、利于阅览");
		changer.replaceBookMark(content,null);

		//保存替换后的WORD
		changer.saveAs("/media/zhangjun/新加卷/","after.docx");
		System.out.println("time=="+(System.currentTimeMillis() - startTime));

	}


	/** 内部使用的文档对象 **/
	private XWPFDocument document;

	private BookMarks    bookMarks = null;

	/**
	 * 为文档设置模板
	 * @param templatePath  模板文件名称
	 */
	public void setTemplate(String templatePath) {
		try {
			this.document = new XWPFDocument(
					POIXMLDocument.openPackage(templatePath));

			bookMarks = new BookMarks(document);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * 进行标签替换的例子,传入的Map中，key表示标签名称，value是替换的信息
	 * @param indicator
	 */
	public void  replaceBookMark(Map<String,String> indicator,Map<String,String> inTableParam) {
		//循环进行替换
		Iterator<String> bookMarkIter = bookMarks.getNameIterator();
		while (bookMarkIter.hasNext()) {
			String bookMarkName = bookMarkIter.next();

			//得到标签名称
			BookMark bookMark = bookMarks.getBookmark(bookMarkName);

			//进行替换
			if (indicator.get(bookMarkName)!=null) {
				if(bookMark.isInTable()){
					inTableParam.put(bookMarkName,indicator.get(bookMarkName));
				}else {
					bookMark.insertTextAtBookMark(indicator.get(bookMarkName), BookMark.INSERT_BEFORE);
				}
			}

		}

	}

	public void fillTableAtBookMark(String bookMarkName,List<Map<String,String>> content) {

		//rowNum来比较标签在表格的哪一行
		int rowNum = 0;

		//首先得到标签
		BookMark bookMark = bookMarks.getBookmark(bookMarkName);
		Map<String, String> columnMap = new HashMap<String, String>();
		Map<String, Node> styleNode = new HashMap<String, Node>();

		//标签是否处于表格内
		if(bookMark.isInTable()){

			//获得标签对应的Table对象和Row对象
			XWPFTable table = bookMark.getContainerTable();
			XWPFTableRow row = bookMark.getContainerTableRow();
			CTRow ctRow = row.getCtRow();
			List<XWPFTableCell> rowCell = row.getTableCells();
			for(int i = 0; i < rowCell.size(); i++){
				columnMap.put(i+"", rowCell.get(i).getText().trim());
				//System.out.println(rowCell.get(i).getParagraphs().get(0).createRun().getFontSize());
				//System.out.println(rowCell.get(i).getParagraphs().get(0).getCTP());
				//System.out.println(rowCell.get(i).getParagraphs().get(0).getStyle());

				//获取该单元格段落的xml，得到根节点
				Node node1 = rowCell.get(i).getParagraphs().get(0).getCTP().getDomNode();

				//遍历根节点的所有子节点
				for (int x=0;x<node1.getChildNodes().getLength();x++) {
					if (node1.getChildNodes().item(x).getNodeName().equals(BookMark.RUN_NODE_NAME)) {
						Node node2 = node1.getChildNodes().item(x);

						//遍历所有节点为"w:r"的所有自己点，找到节点名为"w:rPr"的节点
						for (int y=0;y<node2.getChildNodes().getLength();y++) {
							if(node2.getChildNodes().item(y).getNodeName().endsWith(BookMark.STYLE_NODE_NAME)){

								//将节点为"w:rPr"的节点(字体格式)存到HashMap中
								styleNode.put(i+"", node2.getChildNodes().item(y));
							}
						}
					} else {
						continue;
					}
				}
			}

			//循环对比，找到该行所处的位置，删除改行
			for(int i = 0; i < table.getNumberOfRows(); i++){
				if(table.getRow(i).equals(row)){
					rowNum = i;
					break;
				}
			}
			table.removeRow(rowNum);

			for(int i = 0; i < content.size(); i++){
				//创建新的一行,单元格数是表的第一行的单元格数,
				//后面添加数据时，要判断单元格数是否一致
				XWPFTableRow tableRow = table.createRow();
				CTTrPr trPr = tableRow.getCtRow().addNewTrPr();
				CTHeight ht = trPr.addNewTrHeight();
				ht.setVal(BigInteger.valueOf(360));
			}

			//得到表格行数
			int rcount = table.getNumberOfRows();
			for(int i = rowNum; i < rcount; i++){
				XWPFTableRow newRow = table.getRow(i);

				//判断newRow的单元格数是不是该书签所在行的单元格数
				if(newRow.getTableCells().size() != rowCell.size()){

					//计算newRow和书签所在行单元格数差的绝对值
					//如果newRow的单元格数多于书签所在行的单元格数，不能通过此方法来处理，可以通过表格中文本的替换来完成
					//如果newRow的单元格数少于书签所在行的单元格数，要将少的单元格补上
					int sub= Math.abs(newRow.getTableCells().size() - rowCell.size());
					//将缺少的单元格补上
					for(int j = 0;j < sub; j++){
						newRow.addNewTableCell();
					}
				}

				List<XWPFTableCell> cells = newRow.getTableCells();

				for(int j = 0; j < cells.size(); j++){
					XWPFParagraph para = cells.get(j).getParagraphs().get(0);
					XWPFRun run = para.createRun();
					if(content.get(i-rowNum).get(columnMap.get(j+"")) != null){

						//改变单元格的值，标题栏不用改变单元格的值
						run.setText(content.get(i-rowNum).get(columnMap.get(j+""))+"");

						//将单元格段落的字体格式设为原来单元格的字体格式
						run.getCTR().getDomNode().insertBefore(styleNode.get(j+"").cloneNode(true), run.getCTR().getDomNode().getFirstChild());
					}

					para.setAlignment(ParagraphAlignment.CENTER);
				}
			}
		}
	}

	public void replaceText(Map<String,String> bookmarkMap, String bookMarkName) {

		//首先得到标签
		BookMark bookMark = bookMarks.getBookmark(bookMarkName);
		//获得书签标记的表格
		XWPFTable table = bookMark.getContainerTable();
		//获得所有的表
		//Iterator<XWPFTable> it = document.getTablesIterator();

		if(table != null){
			//得到该表的所有行
			int rcount = table.getNumberOfRows();
			for(int i = 0 ;i < rcount; i++){
				XWPFTableRow row = table.getRow(i);

				//获到改行的所有单元格
				List<XWPFTableCell> cells = row.getTableCells();
				for(XWPFTableCell c : cells){
					for(Entry<String,String> e : bookmarkMap.entrySet()){
						if(c.getText().equals(e.getKey())){
							//删掉单元格内容
							c.removeParagraph(0);

							//给单元格赋值
							c.setText(e.getValue());
						}
					}
				}
			}
		}
	}

	public void saveAs(String toPath,String fileName) {
		File path = new File(toPath);
		path.mkdirs();
		File newFile=new File(toPath+fileName);
		if(!newFile.exists()){
			try {
				newFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(newFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.document.write(fos);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
