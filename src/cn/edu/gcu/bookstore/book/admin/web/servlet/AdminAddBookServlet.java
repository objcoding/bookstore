package cn.edu.gcu.bookstore.book.admin.web.servlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import cn.edu.gcu.bookstore.book.domain.Book;
import cn.edu.gcu.bookstore.book.service.BookService;
import cn.edu.gcu.bookstore.category.domain.Category;
import cn.edu.gcu.bookstore.category.service.CategoryService;
import cn.itcast.commons.CommonUtils;

/**
 * **
 * Servlet implementation class AdminAddBookServlet
 */
@WebServlet("/admin/AdminAddBookServlet")
public class AdminAddBookServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	//依赖的业务逻辑层
	BookService bookService = new BookService();
	CategoryService categoryService = new CategoryService();
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		/*
		 * 一.上传三大步
		 */
		//解析工厂
		DiskFileItemFactory factory = new DiskFileItemFactory(15 * 1024, new File("E:/workspace/temp"));
		//解析器
		ServletFileUpload sfu = new ServletFileUpload(factory);
		//设置单个文件上传大小
		sfu.setSizeMax(15 * 1024);
		//解析request对象
		try {
			@SuppressWarnings("unchecked")
			List<FileItem> fileItemList = sfu.parseRequest(request);
			/*
			 * 把fileItemList中的数据封装到Book对象中
			 * 1,先创建一个map,
			 * 2,把每个item存在map中
			 * 把map转换成book对象
			 */
			Map<String, String> map = new HashMap<String, String>();
			for (FileItem fileItem : fileItemList) {
				if (fileItem.isFormField()) {
					map.put(fileItem.getFieldName(), fileItem.getString("utf-8"));
				}
			}
			Book book = CommonUtils.toBean(map, Book.class);
			/*
			 * 这里还需要把category加进book对象中
			 */
			Category category = CommonUtils.toBean(map, Category.class);
			book.setCategory(category);
			
			/*
			 * 二. 保存上传的文件
			 * 1,文件目录
			 * 2,文件名称
			 */
			//创建保存文件路径
			String savepath = this.getServletContext().getRealPath("/book_img");
			//得到文件名称
			String filename = CommonUtils.uuid() + "_" + fileItemList.get(1).getName();
			//创建目标文件
			File destFile = new File(savepath, filename);
			
			//写
			try {
				fileItemList.get(1).write(destFile);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			
			/*三.设置book对象的image属性与bid
			 */
			book.setImage("book_img/" + filename);
			
			//设置bid
			book.setBid(CommonUtils.uuid());

			/*
			 * 四.保存book
			 */
			bookService.add(book);
			/*
			 * 五.返回图示列表
			 */
			request.getRequestDispatcher("/admin/AdminBookServlet?method=findAll").forward(request, response);
			
		} catch (Exception e) {
			if(e instanceof FileUploadBase.FileSizeLimitExceededException){
				request.setAttribute("msg", "您上传的文件大小超出额定范围");
				request.setAttribute("categoryList", categoryService.findAll());
				request.getRequestDispatcher("/adminjsps/admin/book/add.jsp").forward(request, response);
			}
		}
		
	}

}
