package cn.edu.gcu.bookstore.category.admin.web.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.edu.gcu.bookstore.category.domain.Category;
import cn.edu.gcu.bookstore.category.service.CategoryService;
import cn.edu.gcu.bookstore.category.service.CategoryServiceException;
import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

@WebServlet("/admin/AdminCategoryServlet")
public class AdminCategoryServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	//依赖的业务逻辑层
	private CategoryService categoryService = new CategoryService();
      /**
       * 得到所有分类
       * @param request
       * @param response
       * @return
       * @throws ServletException
       * @throws IOException
       */
	public String findAll(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setAttribute("categoryList", categoryService.findAll());
		return "f:/adminjsps/admin/category/list.jsp";
	}
	/**
	 * 添加分类,该分类不能重名不能空白
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String add(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Category category = CommonUtils.toBean(request.getParameterMap(), Category.class);
		category.setCid(CommonUtils.uuid());//由于表单没有,需补全
		try {
			categoryService.add(category);
			return findAll(request, response);//最终还是回到list.jsp
		} catch (CategoryServiceException e) {
			request.setAttribute("msg", e.getMessage());
			return "f:/adminjsps/msg.jsp";
		}
	}
	/**
	 * 删除分类,该分类有图书就不能删除
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String delete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String cid = request.getParameter("cid");
		try {
			categoryService.delete(cid);
			List<Category> categories = categoryService.findAll();
			request.setAttribute("categoryList", categories);
			return "f:/adminjsps/admin/category/list.jsp";
		} catch (CategoryServiceException e) {
			request.setAttribute("msg", e.getMessage());
			return "f:/adminjsps/msg.jsp";
		}
	}
	/**
	 * 修改分类前的加载工作
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String editPre(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Category category = categoryService.load(request.getParameter("cid"));
		request.setAttribute("category", category);
		return "f:/adminjsps/admin/category/mod.jsp";
	}
	/**
	 * 修改分类
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String edit(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Category category = CommonUtils.toBean(request.getParameterMap(), Category.class);
		categoryService.edit(category);
		return findAll(request, response);
	}
}
