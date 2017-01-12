package cn.edu.gcu.bookstore.cart.domain;

import java.math.BigDecimal;

import cn.edu.gcu.bookstore.book.domain.Book;

public class CartItem {
	
	private Book book;
	private int count;
	
	public double getSubTotal(){
		//处理了二进制运算误差问题
		BigDecimal d1 = new BigDecimal(book.getPrice() + "");
		BigDecimal d2 = new BigDecimal(count + "");
		return d1.multiply(d2).doubleValue();
	}
	
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	@Override
	public String toString() {
		return "CartItem [Book=" + book + ", count=" + count + "]";
	}
}
