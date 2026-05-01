import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmpDao {

	public static Connection getConnection() {
		try {
			return DBUtil.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException("Unable to connect to database", e);
		}
	}

	public static int save(Emp e){
		int state=0;
		try(Connection con=EmpDao.getConnection();
			PreparedStatement ps=con.prepareStatement("insert into users(name,date,email,city,status_value) values (?,?,?,?,?)")){
			ps.setString(1,e.getName());
			ps.setString(2,e.getDate());
			ps.setString(3,e.getEmail());
			ps.setString(4,e.getCity());
			ps.setString(5,e.getStatus());
			state=ps.executeUpdate();
		}catch(Exception ex){ex.printStackTrace();}

		return state;
	}

	public static int updateallow(int id){
		int status=0;
		try(Connection con=EmpDao.getConnection();
			PreparedStatement ps=con.prepareStatement("update users set status_value=? where id=?")){
			ps.setString(1,"allowed");
			ps.setInt(2,id);
			status=ps.executeUpdate();
		}catch(Exception ex){ex.printStackTrace();}

		return status;
	}

	public static int updateblock(int id){
		int status=0;
		try(Connection con=EmpDao.getConnection();
			PreparedStatement ps=con.prepareStatement("update users set status_value=? where id=?")){
			ps.setString(1,"blocked");
			ps.setInt(2,id);
			status=ps.executeUpdate();
		}catch(Exception ex){ex.printStackTrace();}

		return status;
	}

	public static List<Emp> getAllusersbydate(String date){
		List<Emp> list=new ArrayList<Emp>();

		try(Connection con=EmpDao.getConnection();
			PreparedStatement ps=con.prepareStatement("select * from users where date= ? order by id desc")){
			ps.setString(1,date);
			ResultSet rs=ps.executeQuery();
			while(rs.next()){
				Emp e=new Emp();
				e.setId(rs.getInt(1));
				e.setName(rs.getString(2));
				e.setDate(rs.getString(3));
				e.setEmail(rs.getString(4));
				e.setCity(rs.getString(5));
				e.setStatus(rs.getString(6));
				list.add(e);
			}
		}catch(Exception e){e.printStackTrace();}

		return list;
	}

	public static void saveSession(String name,String sessid){
		try(Connection con=EmpDao.getConnection();
			PreparedStatement ps=con.prepareStatement("insert into session_store(name,sessid) values (?,?)")){
			ps.setString(1,name);
			ps.setString(2,sessid);
			ps.executeUpdate();
		}catch(Exception ex){ex.printStackTrace();}

	}

	public static int validateUser(String csessid){
		int status=0;
		try(Connection con=EmpDao.getConnection();
			PreparedStatement ps=con.prepareStatement("select * from session_store where sessid=?")){
			ps.setString(1,csessid);
			ResultSet rs=ps.executeQuery();
	          if(rs.next()) {
	        	   status=1;
	          }
		}catch(Exception e){e.printStackTrace();}
		return status;
	}
}
