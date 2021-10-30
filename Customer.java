package guipcbang;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

class Customer extends JFrame {

	final int FIELDCNT = 10;
	JPanel customerPnl, p, buttonPanel;
	JTextField[] tf = new JTextField[FIELDCNT - 1];
	JLabel[] label = new JLabel[FIELDCNT];
	String[] s = { "ID", "등급", "이름", "생년월일", "연락처", "E-mail", "거주지", "남은 시간", "마일리지", "총이용금액" };
	JButton[] btn = new JButton[3];
	String[] b = { " VIEW ", "Update", "Delete" };
	JComboBox<String> cbGrade;
	DefaultTableModel ctbm;
	JTable ctb;
	JScrollPane sp;
	String[] data = new String[FIELDCNT];
	String cbG;
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	Date value = new Date();
	String now = df.format(value);
	MemberHandler mh = new MemberHandler();
	String pw;

	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;

	public Customer() {
		this.setTitle("회 원 관 리");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.newComponent();
		this.setComponent();
		this.addComponent();
		this.pack();
		this.setVisible(true);
	}

	public void newComponent() {
		ctbm = new DefaultTableModel(null, s);
		ctb = new JTable(ctbm);
		customerPnl = new JPanel(new BorderLayout());
		p = new JPanel(new GridLayout(0, 10));
		buttonPanel = new JPanel();
		cbGrade = new JComboBox<String>();
		for (int i = 0; i < FIELDCNT; i++)
			label[i] = new JLabel(s[i], JLabel.CENTER);
		for (int i = 0; i < FIELDCNT - 1; i++)
			tf[i] = new JTextField(10);

		for (int i = 0; i < 3; i++) {
			btn[i] = new JButton(b[i]);
		}
		sp = new JScrollPane(ctb);
	}

	public void setComponent() {
		tf[0].setBackground(Color.YELLOW);
		tf[2].setText(now);

		ctb.setBackground(Color.LIGHT_GRAY);
		ctb.setFont(new Font("배달의민족 한나체 Air", Font.BOLD, 15));
	}

	public void addComponent() {
		tf[0].addActionListener(mh);
		loadGrade();
		for (int i = 0; i < FIELDCNT; i++) {
			p.add(label[i]);
			if (i == 0)
				p.add(tf[i]);
			else if (i == 1)
				p.add(cbGrade);
			else
				p.add(tf[i - 1]);
		}
		for (int i = 0; i < 3; i++) {
			btn[i].addActionListener(mh);
			buttonPanel.add(btn[i]);
		}

		DefaultTableCellRenderer ca = new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				for (int i = 0; i < FIELDCNT; i++) {
					if (column <= 7) {
						setHorizontalAlignment(SwingConstants.CENTER);
					} else if (column >= 8) {
						setHorizontalAlignment(SwingConstants.RIGHT);
					}
				}
				return this;
			}
		};
		for (int i = 0; i < FIELDCNT; i++)
			ctb.getColumnModel().getColumn(i).setCellRenderer(ca);

		customerPnl.add(p, BorderLayout.NORTH);
		customerPnl.add(sp, BorderLayout.CENTER);
		customerPnl.add(buttonPanel, BorderLayout.SOUTH);
		this.add(customerPnl);
		viewButtons();
	}

	class MemberHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			makeConnection();
			try {
				stmt = con.createStatement();
				if (e.getSource() == tf[0]) {
					tf[1].requestFocus();
					if (isExist()) {
						getData();
						tf[0].setText(data[0]);
						tf[1].setText(data[2]);
						tf[2].setText(data[3]);
						tf[3].setText(data[4]);
						tf[4].setText(data[5]);
						tf[5].setText(data[6]);
						tf[6].setText(data[7]);
						tf[7].setText(data[8]);
						tf[8].setText(data[9]);

						cbGrade.setSelectedItem(data[1]);

						updateButtons();
//						btn[3].setEnabled(true);
					} else {
						tf[1].requestFocus();
						clearField();
						appendButtons();
//						btn[3].setEnabled(true);
					}
				} else if (e.getSource() == btn[0]) {
					viewData();
				} else if (e.getSource() == btn[1]) {
					updateData();
					viewData();
				} else if (e.getSource() == btn[2]) {
					deleteData();
					viewData();
				}
			} catch (SQLException sqle) {
				System.out.println(sqle.getMessage());
			}
			disConnection();
		}
	}

	public void viewData() throws SQLException {
		String sql = "";
		ctbm.setNumRows(0);
		sql = "SELECT * FROM customer as s INNER JOIN code as c " + "ON s.grade=c.code order by s.id";
		try {
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				getData();
				ctbm.addRow(data);
			}
		} catch (SQLException sqle) {
			System.out.println("viewData: SQL Error");
		}
		clearTextField();
		viewButtons();
		tf[0].requestFocus();
	}

	public void updateData() throws SQLException {
		setData();
		String sql = "";
		sql = "UPDATE customer SET pw='" + pw + "',grade='" + data[1] + "',name='" + data[2] + "',birth='" + data[3]
				+ "',phone='" + data[4] + "',mail='" + data[5] + "',address='" + data[6] + "',remaintime='" + data[7]
				+ "',point='" + data[8] + "',totalusingmoney='" + data[9] + "' WHERE id='" + data[0] + "'";
		System.out.println(sql);
		stmt.executeUpdate(sql);
		clearTextField();
		tf[0].requestFocus();
	}

	public void deleteData() throws SQLException {
		setData();
		String sql = "";
		sql = "DELETE FROM customer WHERE id='" + data[0] + "'";
		System.out.println(sql);
		stmt.executeUpdate(sql);
		clearTextField();
		tf[0].requestFocus();
	}

//	public void appendData() throws SQLException{
//		setData();
//		String sql="";
//		sql="INSERT INTO customer (id,grade,name,birth,phone,mail,address,remaintime,point,totalusingmoney) values ";
//		sql+="('"+data[0]+"','"+data[1]+"','"+data[2]+"','"+data[3]+"','"+data[4]+"','"+data[5]+"','"+data[6]+"','"+data[7]+"','"+data[8]+"','"+data[9]+"')";
//		System.out.println(sql);
//		int isAppended=stmt.executeUpdate(sql);
//		if(isAppended==1)
//			System.out.println("Appended Successfully.");
//		else
//			System.out.println("Append Failed.");
//		clearTextField();
//		tf[0].requestFocus();		
//	}

	public boolean isExist() {
		String sql = "";
		boolean isExist = false;
		sql = "SELECT * FROM customer as s INNER JOIN code as c " + "ON s.grade=c.code WHERE id='" + tf[0].getText()
				+ "'";
		try {
			rs = stmt.executeQuery(sql);
			if (rs.next())
				isExist = true;
		} catch (SQLException sqle) {
			System.out.println("isExist: SQL Error");
		}
		return isExist;
	}

	public void getData() throws SQLException {
		data[0] = rs.getString("id");
		data[1] = rs.getString("grade");
		data[2] = rs.getString("name");
		data[3] = rs.getString("birth");
		data[4] = rs.getString("phone");
		data[5] = rs.getString("mail");
		data[6] = rs.getString("address");
		data[7] = rs.getString("remaintime");
		data[8] = rs.getString("point");
		data[9] = rs.getString("totalusingmoney");
		pw = rs.getString("pw");

		cbG = rs.getString("c.code") + ":" + rs.getString("c.name");

		cbGrade.setSelectedItem(cbG);
	}

	public void setData() throws SQLException {
		String s[];
		data[0] = tf[0].getText();
		s = cbGrade.getSelectedItem().toString().split(":");
		data[1] = s[0];
		data[2] = tf[1].getText();
		data[3] = tf[2].getText();
		data[4] = tf[3].getText();
		data[5] = tf[4].getText();
		data[6] = tf[5].getText();
		data[7] = tf[6].getText();
		data[8] = tf[7].getText();

	}

	public void clearTextField() {
		for (int i = 0; i < FIELDCNT - 1; i++) {
			tf[i].setText("");
		}
	}

	public void clearField() {
		for (int i = 1; i < FIELDCNT - 1; i++) {
			tf[i].setText("");
		}
	}

	public void viewButtons() {
		btn[0].setEnabled(true);
		btn[1].setEnabled(false);
		btn[2].setEnabled(false);
//		btn[3].setEnabled(false);
	}

	public void appendButtons() {
		btn[0].setEnabled(true);
		btn[1].setEnabled(false);
		btn[2].setEnabled(false);
//		btn[3].setEnabled(true);
	}

	public void updateButtons() {
		btn[0].setEnabled(true);
		btn[1].setEnabled(true);
		btn[2].setEnabled(true);
//		btn[3].setEnabled(false);
	}

	public void loadGrade() {
		makeConnection();
		String sql = "";
		sql = "SELECT * FROM code where type='회원등급' order by code";
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				cbGrade.addItem((rs.getString("code")) + ":" + rs.getString("name"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		disConnection();
	}

	public Connection makeConnection() {
		String url = "jdbc:mysql://localhost:3306/pcbang_db?serverTimezone=Asia/Seoul";
		String id = "root";
		String password = "1234";
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("드라이브 적재 성공");
			con = DriverManager.getConnection(url, id, password);
			System.out.println("데이터베이스 연결 성공");
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버를 찾을 수 없습니다");
			e.getStackTrace();
		} catch (SQLException e) {
			System.out.println("연결에 실패하였습니다");
			System.out.println("SQLEXception" + e.getMessage());
			System.out.println("SQLState" + e.getSQLState());
			System.out.println("VendorError" + e.getErrorCode());
		}
		return con;
	}

	public void disConnection() {
		try {
			rs.close();
			stmt.close();
			con.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args) {
		new Customer();
	}
}