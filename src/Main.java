import net.proteanit.sql.DbUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.*;
public class Main {
    public static void main(String[] args) {

        login();
    }

    public static void login(){//The login method, which handles creating the login window and logging in users
        JFrame f = new JFrame();//Create a JFrame for the login window
        JLabel l1,l2;

        l1 = new JLabel("Username:");
        l1.setBounds(30,15,100,30);

        l2 = new JLabel("Password:");
        l2.setBounds(30,45,100,30);

        JTextField F_user = new JTextField();
        F_user.setBounds(110,15,200,30);

        JPasswordField F_pass = new JPasswordField();
        F_pass.setBounds(110,50,200,30);

        JButton login_but = new JButton("Login");
        login_but.setBounds(130,90,80,25);
        login_but.addActionListener(new ActionListener() {//Login button implementation
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = F_user.getText();
                String password = F_pass.getText();

                if (username.equals("")){
                    JOptionPane.showMessageDialog(null,"Please enter username");
                } else if (password.equals("")) {
                    JOptionPane.showMessageDialog(null,"Please enter password");
                }else{
                    Connection connection = connect();
                    try{//Try connecting to the database and retrieving user data
                        Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                        String st = "SELECT * FROM MYDB.USERS WHERE USERNAME='"+username+"' AND PASSWORD = '"+password+"'";
                        ResultSet rs=stmt.executeQuery(st);
                        if (!rs.next()){
                            System.out.println("No such user!");
                            JOptionPane.showMessageDialog(null,"Wrong username/password!");
                        }else {
                            f.dispose();
                            rs.beforeFirst();
                            while (rs.next()){
                                String admin = rs.getString("ADMIN");
                                String UID=rs.getString("UID");
                                if(admin.equals("1")){
                                    admin_menu();
                                }else {
                                    user_menu(UID);
                                }
                            }
                        }


                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }

            }
        });
        f.add(F_user);
        f.add(F_pass);
        f.add(l1);
        f.add(l2);
        f.add(login_but);

        f.setSize(400,180);
        f.setLayout(null);
        f.setVisible(true);
        f.setLocationRelativeTo(null);


    }
    public static Connection connect() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/mysql?user=root&password=devrootpassword");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(connection);
        return connection;
    }

    public static void user_menu(String UID){
        JFrame f=new JFrame("User functions");
        JButton view_but = new JButton("View books");
        view_but.setBounds(20,20,120,25);
        view_but.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame f = new JFrame("Books available");
                Connection connection = connect();
                String sql = "SELECT * FROM BOOKS";
                try{
                    Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                    stmt.executeUpdate("USE MYDB");
                    stmt=connection.createStatement();
                    ResultSet rs=stmt.executeQuery(sql);
                    JTable book_list = new JTable();
                    book_list.setModel(DbUtils.resultSetToTableModel(rs));
                    JScrollPane scrollPane = new JScrollPane(book_list);

                    f.add(scrollPane);
                    f.setSize(800,400);
                    f.setVisible(true);
                    f.setLocationRelativeTo(null);
                }
                catch (SQLException exx) {
                    JOptionPane.showMessageDialog(null,exx);
                }
            }
        });
        JButton my_book= new JButton("My books");
        my_book.setBounds(150,20,120,25);
        my_book.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame f = new JFrame("My books");
                int UID_int = Integer.parseInt(UID);

                Connection connection = connect();
                String sql = "SELECT DISTINCT MYDB.ISSUED.*,BOOKS.BNAME,BOOKS.GENRE, BOOKS.PRICE FROM MYDB.ISSUED, MYDB.BOOKS WHERE ((MYDB.ISSUED.UID="+UID_int+") and (MYDB.BOOKS.BID in (SELECT BID FROM MYDB.ISSUED WHERE MYDB.ISSUED.UID="+UID_int+"))) GROUP BY IID,BNAME";
                String sql2= "SELECT BID FROM ISSUED WHERE UID = "+UID_int;
                try{
                    Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                    stmt.executeUpdate("USE MYDB");
                    stmt=connection.createStatement();
                    ArrayList books_list = new ArrayList();
                    ResultSet rs = stmt.executeQuery(sql);
                    JTable book_list = new JTable();
                    book_list.setModel(DbUtils.resultSetToTableModel(rs));
                    JScrollPane scrollPane = new JScrollPane(book_list);
                    f.add(scrollPane);
                    f.setSize(800,400);
                    f.setVisible(true);
                    f.setLocationRelativeTo(null);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null,ex);
                }

            }
        });
        f.add(my_book);
        f.add(view_but);
        f.setSize(300,100);
        f.setVisible(true);
        f.setLocationRelativeTo(null);
    }

    public static void admin_menu(){

    }
}
