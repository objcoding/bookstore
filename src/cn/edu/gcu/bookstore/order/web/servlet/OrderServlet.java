package cn.edu.gcu.bookstore.order.web.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.edu.gcu.bookstore.cart.domain.Cart;
import cn.edu.gcu.bookstore.cart.domain.CartItem;
import cn.edu.gcu.bookstore.order.domain.Order;
import cn.edu.gcu.bookstore.order.domain.OrderItem;
import cn.edu.gcu.bookstore.order.service.OrderException;
import cn.edu.gcu.bookstore.order.service.OrderService;
import cn.edu.gcu.bookstore.user.domain.User;
import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

@WebServlet("/OrderServlet")
public class OrderServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	
	OrderService orderService = new OrderService();
	
	/**
	 * 为一个用户添加订单
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String add(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		//从session获取购物车
		Cart cart = (Cart) request.getSession().getAttribute("cart");
		/*
		 * 创建订单
		 */
		Order order = new Order();
		order.setOid(CommonUtils.uuid());
		order.setOrdertime(new Date());
		order.setState(1);//未付款
		User user = (User)request.getSession().getAttribute("session_user");
		order.setOwner(user);
		order.setTotal(cart.getTotal());
		
		/*
		 * 创建订单条目集合
		 */
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		//遍历cart中所有cartItem,然后对应生成OrderItem对象,并添加到集合中
		for(CartItem cartItem : cart.getCartItems()){
			OrderItem orderItem = new OrderItem();
			
			orderItem.setIid(CommonUtils.uuid());
			orderItem.setCount(cartItem.getCount());
			orderItem.setBook(cartItem.getBook());
			orderItem.setSubtotal(cartItem.getSubTotal());
			orderItem.setOrder(order);
			
			orderItemList.add(orderItem);
		}
		//把所有条目添加到订单中
		order.setOrderItemList(orderItemList);
		//清空购物车
		cart.clear();
		
		/*
		 * 调用service
		 */
		orderService.add(order);
		/*
		 * 保存order到reques
		 */
		request.setAttribute("order", order);
		
		return "f:/jsps/order/desc.jsp";
	}
	/**
	 * 通过uid加载该用户所有的订单
	 * @param request
	 * @param response
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public String myOrders(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		/*
		 * 1,从session中获取用户获取uid
		 * 2,通过uid获取订单集合
		 * 3,保存到request域中
		 */
		User user = (User)request.getSession().getAttribute("session_user");
		List<Order> orderList = orderService.myOrders(user.getUid());
		request.setAttribute("orderList", orderList);
		
		return "f:/jsps/order/list.jsp";
	}
	/**
	 * 通过oid查询得到一个订单
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String load(HttpServletRequest request, HttpServletResponse response)
			throws  ServletException, IOException{
		
		String oid = request.getParameter("oid");
		Order order = orderService.load(oid);
		request.setAttribute("order", order);
		return "f:/jsps/order/desc.jsp";
	}
	/**
	 * 确认收货
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String confirm(HttpServletRequest request, HttpServletResponse response)
			throws  ServletException, IOException{
		
		try {
			orderService.confirm(request.getParameter("oid"));
			request.setAttribute("msg", "交易成功!");
		} catch (OrderException e) {
			request.setAttribute("msg", e.getMessage());
		}
		
		return "f:/jsps/msg.jsp";
	}
	/**
	 * 这是一个模拟支付方式的方法,仅仅模拟一个效果而已
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String pay(HttpServletRequest request, HttpServletResponse response)
			throws  ServletException, IOException{
		orderService.payConfirm(request.getParameter("oid"));
		request.setAttribute("msg", "支付成功,请等到发货!");
		return "f:/jsps/msg.jsp";
	}
	/*
	public String back(HttpServletRequest request, HttpServletResponse response)
			throws  ServletException, IOException{
		//银行回调方式
		return null;
	}
	*/
}
