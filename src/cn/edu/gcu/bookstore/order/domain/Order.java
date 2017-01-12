package cn.edu.gcu.bookstore.order.domain;

import java.util.Date;
import java.util.List;

import cn.edu.gcu.bookstore.user.domain.User;

public class Order {
	private String oid;
	private Date ordertime;//下单时间
	private double total;//合计
	private int state;//订单状态,1,未付款 2,已付款但未发货 3,已发货但未确认收货 4,已确认交易成功
	private User owner;//订单所有者,通过uid
	private String address;//收货地址
	
	/*
	 * 用于关联订单条目,每次加载订单,需要把条目也加载进订单对象里! 
	 */
	private List<OrderItem> orderItemList;
	
	public List<OrderItem> getOrderItemList() {
		return orderItemList;
	}
	public void setOrderItemList(List<OrderItem> orderItemList) {
		this.orderItemList = orderItemList;
	}
	
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public Date getOrdertime() {
		return ordertime;
	}
	public void setOrdertime(Date ordertime) {
		this.ordertime = ordertime;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public User getOwner() {
		return owner;
	}
	public void setOwner(User owner) {
		this.owner = owner;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	@Override
	public String toString() {
		return "Order [oid=" + oid + ", ordertime=" + ordertime + ", total=" + total + ", state=" + state + ", owner="
				+ owner + ", address=" + address + "]";
	}
	
}
