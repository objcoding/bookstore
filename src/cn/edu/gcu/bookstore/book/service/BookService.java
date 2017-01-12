package cn.edu.gcu.bookstore.book.service;

import java.util.List;

import cn.edu.gcu.bookstore.book.dao.BookDao;
import cn.edu.gcu.bookstore.book.domain.Book;

public class BookService {
	
	BookDao bookDao = new BookDao();
	/**
	 * 查询所有图书
	 * @return
	 */
	public List<Book> findAll() {
		return bookDao.findAll();
	}
	/**
	 * 按分类查询图书
	 * @param cid
	 * @return
	 */
	public List<Book> findByCategory(String cid) {
		return bookDao.findByCategory(cid);
	}
	/**
	 * 根据图书bid加载图书
	 * @param bid
	 * @return
	 */
	public Book load(String bid) {
		return bookDao.finByBid(bid);
	}
	/**
	 * 添加图书
	 * @param book
	 */
	public void add(Book book) {
		bookDao.add(book);
	}
	/**
	 * 删除图书,但不是真的删除
	 * @param bid
	 */
	public void delete(String bid){
		bookDao.delete(bid);
	}
	/**
	 * 修改图书
	 * @param book
	 */
	public void edit(Book book) {
		bookDao.edit(book);
	}
}
