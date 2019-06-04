import com.sun.star.container.XNameAccess;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.table.XTableRows;
import com.sun.star.text.*;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import ooo.connector.BootstrapSocketConnector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class TestAction {
	private static Log logger = LogFactory.getLog(TestAction.class);
	/**
	 * open office的安装路径;
	 */
	private static String OPENOFFICE_PATH = "/opt/openoffice4/program";
 
	private static XTextDocument mxDoc;
 
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		// 读取文件
		XComponentContext xContext = null;
		try {
			// get the remote office component context
			xContext = BootstrapSocketConnector.bootstrap(OPENOFFICE_PATH);
			System.out.println("Connected to a running office ...");
			// get the remote office service manager
			XMultiComponentFactory xMCF = xContext.getServiceManager();
			Object desktop = xMCF.createInstanceWithContext(
					"com.sun.star.frame.Desktop", xContext);
			XComponentLoader xCompLoader = (XComponentLoader) UnoRuntime
					.queryInterface(XComponentLoader.class, desktop);

			args = new String[] { "/media/zhangjun/新加卷1/raw_3.docx" };
			// args=new String[]{"private:factory/swriter"};
			String sUrl = args[0];
			if (sUrl.indexOf("private:") != 0) {
				java.io.File sourceFile = new java.io.File(args[0]);
				StringBuffer sbTmp = new StringBuffer("file:///");
				sbTmp.append(sourceFile.getCanonicalPath().replace('\\', '/'));
				sUrl = sbTmp.toString();
			}
 
			// Load a Writer document, which will be automaticly displayed
			com.sun.star.lang.XComponent xComp = xCompLoader
					.loadComponentFromURL(sUrl, "_blank", 0,
							new com.sun.star.beans.PropertyValue[0]);
 
			if (xComp != null) {
				mxDoc = (XTextDocument) UnoRuntime.queryInterface(
						XTextDocument.class, xComp);
				
				//XText mxDocText = mxDoc.getText();
 
				// 获取要插入标签的值
				Map contentMap = new HashMap();
				contentMap.put("sx_no", "111");
 
				insertStrToBookMark(contentMap);

 
			} else {
				System.exit(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
 
	}
 
	/**
	 * 
	 * @Description:插入字符串到word文件书签中;
	 * @Title: insertStrToBookMark
	 * @param @param contentMap
	 * @param @throws Exception 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public static void insertStrToBookMark(Map contentMap)
			throws Exception {
		try {
			XText mxDocText = mxDoc.getText();
			// 获取标签集合
			XBookmarksSupplier xBookmarksSupplier = (XBookmarksSupplier) UnoRuntime
					.queryInterface(XBookmarksSupplier.class, mxDoc);
			XNameAccess xNamedBookmarks = xBookmarksSupplier.getBookmarks();
			if (null == xNamedBookmarks) {
				return;
			}
			// 遍历标签集合,并在标签处插入相应的字符串
			for (String bookMark : xNamedBookmarks.getElementNames()) {
				// 判断是否包含要替换内容的书签
				if (contentMap.containsKey(bookMark)) {
					// 从书签位置处开始插入、删除或更改文字或属性
					XTextContent xTextContent = (XTextContent) UnoRuntime
							.queryInterface(XTextContent.class,
									xNamedBookmarks.getByName(bookMark));
					// 获取书签位置
					XTextCursor mxDocCursor = mxDocText
							.createTextCursorByRange(xTextContent.getAnchor());
					XSentenceCursor xSentenceCursor = (XSentenceCursor) UnoRuntime
							.queryInterface(XSentenceCursor.class, mxDocCursor);
					// 插入
					mxDocText.insertString(xSentenceCursor, (String)contentMap.get(bookMark), true);
				}
			}
		} catch (Exception e) {
			//logger.info("插入字符串到文档标签中出错", e);
			e.printStackTrace();
		}
	}
 
	/**
	 * 
	 * @Description: 插入字表格到word文件中;
	 * @Title: insertTable
	 * @param @param mxDoc
	 * @param @param tableMap
	 * @param @throws Exception 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public static void insertTable(Map tableMap) throws Exception {
		try {
			// 首先从文档中查询 XTextTablesSupplier 接口
			XTextTablesSupplier xTablesSupplier = (XTextTablesSupplier) UnoRuntime
					.queryInterface(XTextTablesSupplier.class, mxDoc);
			// 获取表格集合
			XNameAccess xNamedTables = xTablesSupplier.getTextTables();
			// 取表以及表数据 并赋值
			if (null == xNamedTables) {
				return;
			}
			for (String tableName : xNamedTables.getElementNames()) {
				if (tableMap.containsKey(tableName)) {
					XTextTable xTable = (XTextTable) UnoRuntime
							.queryInterface(XTextTable.class,
									xNamedTables.getByName(tableName));
					List<Map> contenList = (List) tableMap.get(tableName);
					if (!contenList.isEmpty() && contenList.size() > 1) {
						boolean isAddTr = contenList.get(0).get("isAddTr")!=null;
						// 获取表行
						XTableRows xRow = xTable.getRows();
						for (int i = 1; i < contenList.size(); i++) {
							Map<String,Object> m = contenList.get(i);
							// 插入列数据
							for (Map.Entry<String,Object> entry :  m.entrySet()) {
								System.out.println("map值" + entry.getKey()
										+ "  " + entry.getValue());
								XText xCellText =  UnoRuntime
										.queryInterface(XText.class, xTable
												.getCellByName(entry.getKey()));
								xCellText
										.setString(entry.getValue().toString());
							}
							// 每行赋值增加一行数据
							if (isAddTr)
								xRow.insertByIndex(xRow.getCount(), 1);
						}
						// 删除最后一行的空白行
						if (isAddTr)
							xRow.removeByIndex(xRow.getCount() - 1, 1);
					}
				}
			}
		} catch (Exception e) {
			logger.info("插入表格到文档中出错", e);
		}
	}
 
	/**
	 * 
	 * @Description: 拼接表格数据
	 * @Title: setTableValue
	 * @param @param tableMap
	 * @param @param tableName
	 * @param @param isAddTr
	 * @param @param tableValueArr
	 * @param @return 设定文件
	 * @return Map 返回类型
	 * @throws
	 */
	public static Map setTableValue(Map tableMap,
			String tableName, boolean isAddTr, String[][] tableValueArr) {
		if (null == tableMap) {
			tableMap = new HashMap();
		}
		String[] cellName = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
				"K", "L", "M", "N" };
		List tableContentList = new ArrayList();
		Map tableContentMap = new HashMap();
		tableContentMap.put("isAddTr", isAddTr);
		tableContentList.add(tableContentMap);
		for (int i = 0; i < tableValueArr.length; i++) {
			tableContentMap = new HashMap();
			for (int j = 0; j < tableValueArr[i].length; j++) {
				tableContentMap.put(cellName[j] + (i + 2), tableValueArr[i][j]);
			}
			tableContentList.add(tableContentMap);
		}
		tableMap.put(tableName, tableContentList);
 
		return tableMap;
	}
 
}