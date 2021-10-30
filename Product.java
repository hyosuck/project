package guipcbang;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


public class Product extends JFrame {
	final int FIELDCNT = 5;
	JPanel productPnl, searchPnl, btnPnl;
	String s[] = { "Code", "종류", "이름", "가격", "수량" };
	JLabel lbl[] = new JLabel[5];
	JTextField tf[] = new JTextField[5];
	String[] b = { "현재 상태 ", "갱신", "삭제", "추가" };
	JButton btn[] = new JButton[4];
	DefaultTableModel ptbm;
	JTable ptb;
	JScrollPane sp;
	String[] items = new String[FIELDCNT];
	String[] data = new String[FIELDCNT];
	MemberHandler mh=new MemberHandler();
	
	Connection con = null;
	Statement stmt = null;
	ResultSet rs=null;

	public Product() {
		setTitle("재 고 관 리");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		
		productPnl=new JPanel(new BorderLayout());

		// 정보 출력부분
		searchPnl = new JPanel();
		for (int i = 0; i < FIELDCNT; i++) {
			lbl[i] = new JLabel(s[i], JLabel.CENTER);
			tf[i] = new JTextField(10);

			searchPnl.add(lbl[i]);
			searchPnl.add(tf[i]);
		}
		tf[0].addActionListener(mh);
		tf[0].setBackground(Color.YELLOW);

		productPnl.add(searchPnl, BorderLayout.NORTH);

		// 테이블 부분
		ptbm = new DefaultTableModel(null, s);
		ptb = new JTable(ptbm);
		sp = new JScrollPane(ptb);
		
		ptb.setBackground(Color.LIGHT_GRAY);
		ptb.setFont(new Font("배달의민족 한나체 Air", Font.BOLD, 15));
		
		DefaultTableCellRenderer ca = new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (column == 0) {
					setHorizontalAlignment(SwingConstants.CENTER);
				} else if (column == 1) {
					setHorizontalAlignment(SwingConstants.CENTER);
				} else if (column == 2) {
					setHorizontalAlignment(SwingConstants.CENTER);
				} else if (column == 3) {
					setHorizontalAlignment(SwingConstants.RIGHT);
				}else {
					setHorizontalAlignment(SwingConstants.RIGHT);
				}
				return this;
			}
		};

		// Cell Size
		ptb.setPreferredScrollableViewportSize(new Dimension(750, 200));
		ptb.getColumnModel().getColumn(0).setPreferredWidth(40);
		ptb.getColumnModel().getColumn(0).setCellRenderer(ca);
		
		ptb.getColumnModel().getColumn(1).setPreferredWidth(50);
		ptb.getColumnModel().getColumn(1).setCellRenderer(ca);
		
		ptb.getColumnModel().getColumn(2).setPreferredWidth(200);
		ptb.getColumnModel().getColumn(2).setCellRenderer(ca);	
		
		ptb.getColumnModel().getColumn(3).setPreferredWidth(50);
		ptb.getColumnModel().getColumn(3).setCellRenderer(ca);
		
		ptb.getColumnModel().getColumn(4).setPreferredWidth(50);
		ptb.getColumnModel().getColumn(4).setCellRenderer(ca);

		productPnl.add(sp, BorderLayout.CENTER);

		// 버튼 부분
		btnPnl = new JPanel();
		for (int i = 0; i < 4; i++) {
			btn[i] = new JButton(b[i]);

			btnPnl.add(btn[i]);
		}
		btn[0].addActionListener(mh);
		btn[1].addActionListener(mh);
		btn[2].addActionListener(mh);
		btn[3].addActionListener(mh);

		productPnl.add(btnPnl, BorderLayout.SOUTH);
		
		//최종 붙이기
		this.add(productPnl);

		setVisible(true);

	}

	class MemberHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			makeConnection();
			try{
				stmt=con.createStatement();
				if(e.getSource()==tf[0]){
					if(isExist()){
						getData();
						for(int i=1;i<FIELDCNT;i++)
							tf[i].setText(data[i]);
						updateButtons();
						btn[3].setEnabled(false);						
					}else{
						tf[1].requestFocus();
						clearField();
						appendButtons();
						btn[3].setEnabled(true);
					}
				}
				else if(e.getSource()==btn[0]){
					viewData();
				}
				else if(e.getSource()==btn[1]){
					updateData();
					viewData();
				}
				else if(e.getSource()==btn[2]){
					deleteData();
					viewData();
				}
				else if(e.getSource()==btn[3]){
					appendData();
					viewData();
				}
			}catch(SQLException sqle){System.out.println(sqle.getMessage());}
			disConnection();
		}
	}
	
	public void viewData() throws SQLException{
		String sql="";
		ptbm.setNumRows(0);
		sql="SELECT * FROM product order by code";
		try{
			rs=stmt.executeQuery(sql);
			while(rs.next()){
				getData();
				ptbm.addRow(data);			
			}
		}catch(SQLException sqle){System.out.println("viewData: SQL Error");}
		clearTextField();
		viewButtons();
		tf[0].requestFocus();		
	}
	
	public void updateData() throws SQLException{
		setData();
		String sql="";
		sql="UPDATE product SET type='"+data[1]+"',name='"+data[2]+"',price='"+data[3]+"',inventory='"+data[4]+"' WHERE code='"+data[0]+"'";
		System.out.println(sql);
		stmt.executeUpdate(sql);
		clearTextField();
		tf[0].requestFocus();
	}

	public void deleteData() throws SQLException{
		setData();
		String sql="";
		sql="DELETE FROM product WHERE code='"+data[0]+"'";
		System.out.println(sql);
		stmt.executeUpdate(sql);
		clearTextField();
		tf[0].requestFocus();
	}

	public void appendData() throws SQLException{
		setData();
		String sql="";
		sql="INSERT INTO product (code, type, name, price, inventory) values ";
		sql+="('"+data[0]+"','"+data[1]+"','"+data[2]+"','"+data[3]+"','"+data[4]+"')";
		System.out.println(sql);
		int isAppended=stmt.executeUpdate(sql);
		if(isAppended==1)
			System.out.println("Appended Successfully.");
		else
			System.out.println("Append Failed.");
		clearTextField();
		tf[0].requestFocus();		
	}
	
	public boolean isExist() throws SQLException{
		String sql="";
		boolean isExist=false;
		sql="SELECT * FROM product WHERE code='"+tf[0].getText()+"'";
		rs=stmt.executeQuery(sql);
		if(rs.next())
			isExist=true;
		return isExist;
	}
	
	public void getData() throws SQLException {
		data[0]=rs.getString("Code");
		data[1]=rs.getString("type");
		data[2]=rs.getString("name");
		data[3]=rs.getString("price");
		data[4]=rs.getString("inventory");
	}
	
	public void setData() throws SQLException {
		for(int i=0;i<FIELDCNT;i++)
			data[i]=tf[i].getText();
	}
	
	public void clearTextField(){
		for(int i=0;i<FIELDCNT;i++){
			tf[i].setText("");
		}
	}
	
	public void clearField(){
		for(int i=1;i<FIELDCNT;i++){
			tf[i].setText("");
		}
	}

	
	public void viewButtons(){
		btn[0].setEnabled(true);
		btn[1].setEnabled(false);
		btn[2].setEnabled(false);
		btn[3].setEnabled(false);
	}

	public void appendButtons(){
		btn[0].setEnabled(true);
		btn[1].setEnabled(false);
		btn[2].setEnabled(false);
		btn[3].setEnabled(true);
	}

	public void updateButtons(){
		btn[0].setEnabled(true);
		btn[1].setEnabled(true);
		btn[2].setEnabled(true);
		btn[3].setEnabled(false);
	}
	
	public Connection makeConnection(){
		String url="jdbc:mysql://localhost:3306/pc?serverTimezone=Asia/Seoul";
		String id="root";
		String password="a3636a";
		try{
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("드라이브 적재 성공");
			con=DriverManager.getConnection(url, id, password);		
			System.out.println("데이터베이스 연결 성공");
		}catch(ClassNotFoundException e){
			System.out.println("드라이버를 찾을 수 없습니다");
			e.getStackTrace();
		}catch(SQLException e){
			System.out.println("연결에 실패하였습니다");
			System.out.println("SQLEXception"+e.getMessage());
			System.out.println("SQLState"+e.getSQLState());
			System.out.println("VendorError"+e.getErrorCode()); 
		}
		return con;
	}

	public void disConnection() {
		try{
			rs.close();
			stmt.close();
			con.close();
		}catch(SQLException e){System.out.println(e.getMessage());}
	}

	public static void main(String[] args) {
		new Product();
	}

}
