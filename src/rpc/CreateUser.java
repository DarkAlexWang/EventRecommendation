package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.client.FindIterable;

import algorithm.GeoRecommendation;
import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class Login
 */
@WebServlet("/createuser")
public class CreateUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateUser() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			DBConnection conn = DBConnectionFactory.getConnection(); 
			try {
				JSONObject obj = new JSONObject(); 
				HttpSession session = request.getSession(false); 
				if (session == null) {
					response.setStatus(403);
					obj.put("status", "Session Invalid"); 
				} else {
						String userId = (String) session.getAttribute("user_id"); 
						String name = conn.getFullname(userId); 
						obj.put("status", "OK");
						obj.put("user_id", userId);
						obj.put("name", name);
				}
				RpcHelper.writeJsonObject(response, obj); 
			} catch (JSONException e) {
				e.printStackTrace();
			}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			DBConnection conn = DBConnectionFactory.getConnection(); 
			try {
				JSONObject input = RpcHelper.readJsonObject(request);
				
				String userId = input.getString("user_id");
				String pwd = input.getString("password");
				String firstname = input.getString("first_name");
				String lastname = input.getString("last_name");
				
				JSONObject obj = new JSONObject();
				System.out.println(conn.checkUser(userId));
				if (!conn.checkUser(userId)) {
						HttpSession session = request.getSession(); 
						session.setAttribute("user_id", userId);
						// setting session to expire in 10 minutes 
						session.setMaxInactiveInterval(10 * 60);
						//CreateUser signup = new CreateUser();
						conn.createUser(userId, pwd, firstname, lastname); 
						
						obj.put("user_id", userId);
						obj.put("first_name", firstname);
						obj.put("last_name", lastname);
						obj.put("status", "OK");
				} else {
					response.setStatus(401);
					obj.put("status", "User exists, please login");
				}
				RpcHelper.writeJsonObject(response, obj); 
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
