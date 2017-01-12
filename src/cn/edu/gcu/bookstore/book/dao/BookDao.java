package cn.edu.gcu.bookstore.book.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.edu.gcu.bookstore.book.domain.Book;
import cn.edu.gcu.bookstore.category.domain.Category;
import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;

public class BookDao {
	QueryRunner qr = new TxQueryRunner();
	/**
	 * 查询所有图书
	 * @return
	 */
	public List<Book> findAll() {
		String sql = "select * from book where del=false";
		try {
			return  qr.query(sql,new BeanListHandler<Book>(Book.class));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 按分类查询图书
	 * @param cid
	 * @return
	 */
	public List<Book> findByCategory(String cid) {
		String sql = "select * from book where cid=? and del=false";
		try {
			return qr.query(sql, new BeanListHandler<Book>(Book.class), cid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 根据图书bid加载图书
	 * @param bid
	 * @return
	 */
	public Book finByBid(String bid) {
		
		String sql = "select * from book where bid=?";
		
		try {
			/*
			 * 由于book表有bid,而book对象只有category,
			 * 因此我们需要把category对象加进book对象中
			 * 这时需要用到map处理集
			 */
			Map<String, Object> map = qr.query(sql, new MapHandler(), bid);
			/*
			 * 把map对象分别映射出book对象和category对象
			 * 再给两个对象添加关系
			 */
			Book book = CommonUtils.toBean(map, Book.class);
			Category category = CommonUtils.toBean(map, Category.class);
			book.setCategory(category);
			return book;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 查询分类下的图书数目
	 * @param cid
	 * @return
	 */
	public int findCountByCid(String cid) {
		String sql = "select count(*) from book where cid=?";
		try {
			Number count = (Number)qr.query(sql, new ScalarHandler(), cid);
			return count.intValue();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 添加图书
	 * @param book
	 */
	public void add(Book book) {
		String sql = "insert into book values(?,?,?,?,?,?)";
		Object[] params = {book.getBid(), book.getBname(), book.getPrice(),
				book.getAuthor(), book.getImage(), book.getCategory().getCid()};
		try {
			qr.update(sql, params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 删除图书,但这是假删除
	 * @param bid
	 */
	public void delete(String bid){
		String sql = "update book set del=true where bid=?";
		try {
			qr.update(sql, bid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 修改图书
	 * @param book
	 */
	public void edit(Book book) {
		String sql = "update book set bname=?,price=?,author=?,image=?,cid=? where bid=?";
		Object[] params = {book.getBname(), book.getPrice(),
				book.getAuthor(), book.getImage(), book.getCategory().getCid(), book.getBid()};
		try {
			qr.update(sql, params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
