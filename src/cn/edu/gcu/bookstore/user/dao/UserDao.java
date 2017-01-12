package cn.edu.gcu.bookstore.user.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import cn.edu.gcu.bookstore.user.domain.User;
import cn.itcast.jdbc.TxQueryRunner;

/**
 * User持久层
 * @author zch
 *
 */
public class UserDao {
	
	private QueryRunner qr = new TxQueryRunner();
	/**
	 * 通过用户名查找用户
	 * @param username
	 * @return
	 */
	public User findByUsername(String username){
		try {
			String sql = "select * from tb_user where username=?";
			return qr.query(sql, new BeanHandler<User>(User.class),username);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 通过email查找用户
	 * @param email
	 * @return
	 */
	public User findByEmail(String email){
		try {
			String sql = "select * from tb_user where email=?";
			return qr.query(sql, new BeanHandler<User>(User.class),email);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 添加用户
	 * @param user
	 */
	public void add(User user){
		try {
			String sql = "insert into tb_user values(?,?,?,?,?,?)";
			Object[] params = {user.getUid(),user.getUsername(),user.getPassword(),
					user.getEmail(),user.getCode(),user.isState()};
			qr.update(sql, params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 通过激活码code查找用户
	 * @param code
	 * @return
	 */
	public User findByCode(String code){
		
		String sql = "select * from tb_user where code=?";
		try {
			return qr.query(sql, new BeanHandler<User>(User.class),code);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 修改用户state激活状态
	 * @param uid
	 * @param state
	 */
	public void updateState(String uid,boolean state){
		
		String sql = "update tb_user set state=? where uid=?";
		Object[] params = {state,uid};
		try {
			qr.update(sql, params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
