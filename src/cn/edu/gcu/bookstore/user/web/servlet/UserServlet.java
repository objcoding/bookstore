package cn.edu.gcu.bookstore.user.web.servlet;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.edu.gcu.bookstore.cart.domain.Cart;
import cn.edu.gcu.bookstore.user.domain.User;
import cn.edu.gcu.bookstore.user.service.UserException;
import cn.edu.gcu.bookstore.user.service.UserService;
import cn.itcast.commons.CommonUtils;
import cn.itcast.mail.Mail;
import cn.itcast.mail.MailUtils;
import cn.itcast.servlet.BaseServlet;

/**
 * User表述层
 * @author zch
 *
 */
@WebServlet("/UserServlet")
public class UserServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	
	private UserService userService = new UserService();
	/**
	 * 注册用户
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String regist(HttpServletRequest request,HttpServletResponse response) 
			throws ServletException, IOException {
		
		User form = CommonUtils.toBean(request.getParameterMap(), User.class);
		//补全表单
		form.setUid(CommonUtils.uuid());
		form.setCode(CommonUtils.uuid()+CommonUtils.uuid());
		/*
		 * 1,输入校验
		 */
		Map<String,String> errors = new HashMap<String , String>();//装在错误信息
		
		String username = form.getUsername();
		if (username == null || username.trim().isEmpty()) {
			errors.put("username", "姓名不能为空");
		}else if (username.length() <= 3 || username.length() > 10){
			errors.put("username", "用户名长度必须在3~10之间");
		}
		
		String password = form.getPassword();
		if (password == null || password.trim().isEmpty()) {
			errors.put("password", "密码不能为空");
		}else if (password.length() <= 3 || password.length() > 10){
			errors.put("password", "密码长度必须在3~10之间");
		}
		
		String email = form.getEmail();
		if (email == null || email.trim().isEmpty()) {
			errors.put("email", "email不能为空");
		}else if (!email.matches("\\w+@\\w+(\\.\\w+)+")){
			errors.put("email", "email格式错误");
		}
		
		if (errors.size() > 0) {
			request.setAttribute("err", errors);
			request.setAttribute("form", form);//回显
			return "f:/jsps/user/regist.jsp";
		}
		/*
		 * 2,调用service,并校验用户名和邮箱是否存在
		 */
		try {
			userService.regist(form);
		} catch (UserException e) {
			request.setAttribute("msg", e.getMessage());
			request.setAttribute("form", form);//回显
			return "f:/jsps/user/regist.jsp";
		}
		/*
		 * 3,发邮件
		 */
		Properties properties = new Properties();
		properties.load(this.getClass().getClassLoader().getResourceAsStream("email_template.properties"));
		
		String host = properties.getProperty("host");
		String uname = properties.getProperty("uname");
		String pwd = properties.getProperty("pwd");
		String from = properties.getProperty("from");
		String to = form.getEmail();
		String subject = properties.getProperty("subject");
		String content = properties.getProperty("content");
		content = MessageFormat.format(content, form.getCode());//利用占位符替换
		
		Session session = MailUtils.createSession(host, uname, pwd);
		Mail mail = new Mail(from,to,subject,content);
		
		try {
			MailUtils.send(session, mail);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		/*
		 * 注册成功,
		 * 1,保存信息
		 * 2,转发msg.jsp
		 */
		request.setAttribute("msg", "恭喜注册成功,请马上去邮箱激活");
		return "f:/jsps/msg.jsp";
		
	}
	/**
	 * 用户激活
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String active(HttpServletRequest request,HttpServletResponse response)
			throws ServletException, IOException{
		
		String code = request.getParameter("code");
		try {
			userService.active(code);
			request.setAttribute("msg", "恭喜激活成功,请登录!");
		} catch (UserException e) {
			request.setAttribute("msg", e.getMessage());
		}
		return "f:/jsps/msg.jsp";
	}
	/**
	 * 用户登录
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String login(HttpServletRequest request,HttpServletResponse response)
			throws ServletException,IOException{
		
		User form = CommonUtils.toBean(request.getParameterMap(), User.class);
		
		try {
			User _form = userService.login(form);
			//System.out.println(_form.getUid());
			HttpSession session = request.getSession();
			session.setAttribute("session_user", _form);
			session.setAttribute("cart", new Cart());//向用户添加一辆购物车
			return "f:/index.jsp";
		} catch (UserException e) {
			request.setAttribute("msg",e.getMessage());
			request.setAttribute("form", form);
			return "f:/jsps/user/login.jsp";
		}
	}
	/**
	 * 用户退出
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String quit(HttpServletRequest request,HttpServletResponse response)
			throws ServletException,IOException{
		request.getSession().invalidate();
		return "r:/jsps/user/login.jsp";
	}
}
