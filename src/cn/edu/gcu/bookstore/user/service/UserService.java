package cn.edu.gcu.bookstore.user.service;

import cn.edu.gcu.bookstore.user.dao.UserDao;
import cn.edu.gcu.bookstore.user.domain.User;
/**
 * 业务逻辑层
 * @author zch
 *
 */
public class UserService {
	
	UserDao userDao = new UserDao();
	/**
	 * 注册用户
	 * @param form
	 * @throws UserException
	 */
	public void regist(User form) throws UserException{
		
		User user = userDao.findByUsername(form.getUsername());
		if (user != null)  throw new UserException("用户已被注册 ");
		user = userDao.findByEmail(form.getEmail());
		if (user != null)  throw new UserException("Email已被注册 ");
		
		userDao.add(form);//注意:这里需要用form
	}
	/**
	 * 用户激活
	 * @param code
	 * @throws UserException
	 */
	public void active(String code) throws UserException{
		
		User user = userDao.findByCode(code);
		if (user == null) throw new UserException("激活码无效");
		if (user.isState()) throw new UserException("用户已激活");
		
		userDao.updateState(user.getUid(), true);//修改用户激活状态
	}
	/**
	 * 用户登录
	 * @param form
	 * @return
	 * @throws UserException
	 */
	public User login(User form) throws UserException{
		
		User user = userDao.findByUsername(form.getUsername());
		if (user == null) throw new UserException("用户不存在");
		if (! user.getPassword().equals(form.getPassword())) throw new UserException("密码错误");
		if (!user.isState()) throw new UserException("你还未激活");
		
		return user;
	}
}
