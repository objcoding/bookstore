package cn.edu.gcu.bookstore.cart.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.edu.gcu.bookstore.book.domain.Book;
import cn.edu.gcu.bookstore.book.service.BookService;
import cn.edu.gcu.bookstore.cart.domain.Cart;
import cn.edu.gcu.bookstore.cart.domain.CartItem;
import cn.itcast.servlet.BaseServlet;

@WebServlet("/CartServlet")
public class CartServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	/**
	 * 添加购物车
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String add(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		/*
		 * 1,得到车
		 * 2,得到条目(图书bid和数量)
		 */
		Cart cart = (Cart) request.getSession().getAttribute("cart");
		//获得条目所需信息
		int count = Integer.parseInt(request.getParameter("count"));
		Book book = new BookService().load(request.getParameter("bid"));
		//创建条目
		CartItem cartItem = new CartItem();
		cartItem.setCount(count);
		cartItem.setBook(book);
		//填加条目到购物车
		cart.add(cartItem);
		
		return "f:/jsps/cart/list.jsp";
	}
	/**
	 * 清空购物车
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String clear(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Cart cart = (Cart) request.getSession().getAttribute("cart");
		cart.clear();
		return "f:/jsps/cart/list.jsp";
	}
	/**
	 * 删除指定条目
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String delete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Cart cart = (Cart) request.getSession().getAttribute("cart");
		cart.delect(request.getParameter("bid"));
		return "f:/jsps/cart/list.jsp";
	}
}
