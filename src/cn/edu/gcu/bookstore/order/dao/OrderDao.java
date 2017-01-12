package cn.edu.gcu.bookstore.order.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.edu.gcu.bookstore.book.domain.Book;
import cn.edu.gcu.bookstore.order.domain.Order;
import cn.edu.gcu.bookstore.order.domain.OrderItem;
import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;

public class OrderDao {
	QueryRunner qr = new TxQueryRunner();
	/**
	 * 生成订单
	 * @param order
	 */
	public void addOrder(Order order){
		String sql = "insert into orders values(?,?,?,?,?,?)";
		/*
		 * 处理utils转换成sql的时间问题
		 */
		Timestamp timestamp = new Timestamp(order.getOrdertime().getTime());
		Object[] params = {order.getOid(),timestamp,order.getTotal(),
				order.getState(),order.getOwner().getUid()/*这里通过用户获取uid*/,order.getAddress()};
		try {
			qr.update(sql, params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 插入订单条目
	 * >>>需要批处理
	 */
	public void addOrderItemList(List<OrderItem> orderItemList){
		
		try {
			String sql = "insert into orderitem values(?,?,?,?,?)";
			/*
			 * 把orderItemList变为一个二维数组,实现批处理
			 */
			Object[][] params = new Object[orderItemList.size()][];
			for(int i = 0; i<orderItemList.size(); i++){
				OrderItem item = orderItemList.get(i);
				params[i] = new Object[] {item.getIid(),item.getCount(),
						item.getSubtotal(),item.getOrder().getOid(),item.getBook().getBid()};
			}
			qr.batch(sql, params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 通过用户名uid查找所属订单,这时订单是一个集合
	 * @param uid
	 * @return
	 */
	public List<Order> findByUid(String uid){
		
		String sql = "select * from orders where uid=?";
			
		try {
			List<Order> orderList = qr.query(sql, new BeanListHandler<Order>(Order.class), uid);
			/*
			 * 为其加载订单条目到订单中,
			 * >>>>>>>>通过oid
			 */
			for (Order order : orderList) {
				loadOrderItems(order);
			}
			return orderList;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} 
	}
	/**
	 * 通过oid加载订单对应所有条目
	 * ***运用多表查询
	 * ***由于两张表,故使用MapListHandler来封装数据
	 * 
	 * @param order
	 */
	private void loadOrderItems(Order order){
		//多表查询
		String sql = "select * from orderitem o, book b where o.bid=b.bid and oid=?";
		try {
			List<Map<String, Object>> mapOrderItemList = qr.query(sql, new MapListHandler(), order.getOid());
			
			/*
			 * mapList包含OrderItem, Book两个对象
			 * >>>需要需要把mapList拆分成两个对象:OrderItem, Book
			 * >>>然后用OrderItem把book添加进来,最终生成一个OrderItem
			 * >>>接着把orderItem添加到List<OrderItem>中
			 * >>>最后再把List<OrderItem>添加到order订单中,并返回
			 */
			
			List<OrderItem> orderItemList = toOrderItemList(mapOrderItemList);//一个map对应生成一个OrderItem对象
			//把所属的所有订单条目添加到对应的订单中
			order.setOrderItemList(orderItemList);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 把mapList遍历,里面的每个map对应生成一个OrderItem对象
	 * 并把OrderItem添加到List<OrderItem>中
	 * @param mapList
	 * @return
	 */
	private List<OrderItem> toOrderItemList(List<Map<String, Object>> mapList) {
		//创建一个list,用于装载OrderItem对象
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		for (Map<String, Object> map : mapList) {
			OrderItem orderItem = toOrderItem(map);
			//把订单条目添加到订单集合中
			orderItemList.add(orderItem);
		}
		return orderItemList;
	}
	/**
	 * 把map生成对应的OrderItem, Book两个对象,并建立关系
	 * @param map
	 * @return
	 */
	private OrderItem toOrderItem(Map<String, Object> map) {
		OrderItem orderItem = CommonUtils.toBean(map, OrderItem.class);
		Book book = CommonUtils.toBean(map, Book.class);
		/*
		 * 把book添加到订单条目中
		 */
		orderItem.setBook(book);
		return orderItem;
	}
	/**
	 * 通过oid查找订单
	 * @param oid
	 * @return
	 */
	public Order findByOid(String oid) {
		String sql = "select * from orders where oid=?";
		try {
			Order order = qr.query(sql, new BeanHandler<Order>(Order.class), oid);
			/*
			 * 为订单加载条目:
			 * 由于订单里面的订单条目还没加载进来
			 * >>>这时需要再次使用loadOrderItems(order)方法来把条目加载进订单对象里
			 */
			loadOrderItems(order);
			return order;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 通过oid查询一个订单的state状态
	 * @param oid
	 * @return
	 */
	public int getStateByOid(String oid){
		String sql = "select state from orders where oid=?";
		try {
			return (Integer) qr.query(sql, new ScalarHandler(),oid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 修改订单状态
	 * @param oid
	 * @param state
	 */
	public void updateState(String oid, int state){
		String sql = "update orders set state=? where oid=?";
		try {
			qr.update(sql, state, oid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
