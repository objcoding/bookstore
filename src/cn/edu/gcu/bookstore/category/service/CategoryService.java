package cn.edu.gcu.bookstore.category.service;

import java.util.List;

import cn.edu.gcu.bookstore.book.dao.BookDao;
import cn.edu.gcu.bookstore.category.dao.CategoryDao;
import cn.edu.gcu.bookstore.category.domain.Category;

public class CategoryService {
	//所有依赖的dao层
	CategoryDao categoryDao = new CategoryDao();
	BookDao bookDao = new BookDao();
	/**
	 * 查询所有分类
	 * @return
	 */
	public List<Category> findAll() {
		return categoryDao.findAll();
	}
	/**
	 * 添加分类,添加前检查该分类是否存在或者该分类是否空白,如果满足,抛异常
	 * @param category
	 * @throws CategoryServiceException 
	 */
	public void add(Category category) throws CategoryServiceException {
		
		List<Category> categories = findAll();
		for (Category c : categories) {
			if (category.getCname().equals(c.getCname())) {
				throw new CategoryServiceException("你添加的分类已经存在");
			}
			if (category.getCname().trim().isEmpty()) {
				throw new CategoryServiceException("请输入分类名");
			}
		}
		categoryDao.add(category);
	}
	/**
	 * 删除分类,删除前检查该分类是否存在图书,存在,抛异常
	 * @param cid
	 * @throws CategoryServiceException
	 */
	public void delete(String cid) throws CategoryServiceException {
		if(bookDao.findCountByCid(cid) > 0) 
			throw new CategoryServiceException("这分类有图书,不能删除此分类");
		categoryDao.delete(cid);
	}
	/**
	 * 加载指定cid的分类信息
	 * @param cid
	 * @return
	 */
	public Category load(String cid) {
		return categoryDao.findCategoryByCid(cid);
	}
	/**
	 * 修改分类
	 * @param category
	 */
	public void edit(Category category) {
		categoryDao.edit(category);
	}
}
