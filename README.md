#bookstore

### 一.功能概览
![image](https://github.com/zchdjb/bookstore/raw/master/WebContent/images/bookstore.png)<br/>

### 二.表结构

![image](https://github.com/zchdjb/bookstore/raw/master/WebContent/images/database.png)<br/>


##难点:
####1.在加载订单的时候,涉及到OrderItem, Book两个对象,需用多表查询,由于两张表,故只能用MapListHandler来封装数据,然后在用小工具一键封装数据拆分成两个对象,再把两个对象映射关系.<br/>



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