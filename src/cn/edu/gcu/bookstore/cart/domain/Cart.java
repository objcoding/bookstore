package cn.edu.gcu.bookstore.cart.domain;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cart {
	/**
	 * 装载购物车条目 , 每次加载购物车, 都需要记得把条目也加载到购物车对象里!
	 */
	private Map<String, CartItem> map = new LinkedHashMap<String,CartItem>();
	
	/**
	 * 合计价格
	 * @return
	 */
	public double getTotal(){
		BigDecimal total = new BigDecimal("0");
		for(CartItem cartItem : map.values()){
			BigDecimal subTotal = new BigDecimal(cartItem.getSubTotal() + "");
			total = total.add(subTotal);
		}
		return total.doubleValue();
	}
	/**
	 * 添加条目
	 * @param cartItem
	 */
	public void add(CartItem cartItem){
		if(map.containsKey(cartItem.getBook().getBid())){
			CartItem _cartItem = map.get(cartItem.getBook().getBid());
			_cartItem.setCount(_cartItem.getCount() + cartItem.getCount());
			map.put(cartItem.getBook().getBid(), _cartItem);
		} else{
			map.put(cartItem.getBook().getBid(), cartItem);
		}
	}
	/**
	 * 清空条目
	 */
	public void clear(){
		map.clear();
	}
	/**
	 * 删除指定条目
	 */
	public void delect(String bid){
		map.remove(bid);
	}
	/**
	 * 返回所有条目
	 * @return
	 */
	public Collection<CartItem> getCartItems(){
		return map.values();
	}
}
