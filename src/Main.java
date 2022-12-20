import net.proteanit.sql.DbUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
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
                try{
                    Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                    stmt.executeUpdate("USE MYDB");
                    stmt=connection.createStatement();
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

        JFrame f = new JFrame();
        JButton view_but = new JButton("View books");
        view_but.setBounds(20,20,120,25);
        view_but.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame f = new JFrame("Books available");

                Connection connection = connect();
                String sql = "SELECT * FROM MYDB.BOOKS";
                try {
                    Statement stmt = connection.createStatement();
                    stmt.executeUpdate("USE MYDB");
                    stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);
                    JTable book_list = new JTable();
                    book_list.setModel(DbUtils.resultSetToTableModel(rs));
                    JScrollPane scrollPane = new JScrollPane(book_list);
                    f.add(scrollPane);
                    f.add(book_list);
                    f.setSize(800,400);
                    f.setVisible(true);
                    f.setLocationRelativeTo(null);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, ex);

                }
            }
        });
        JButton users_but = new JButton("View users");
        users_but.setBounds(150,20,120,25);
        users_but.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame f = new JFrame("Users list");

                Connection connection = connect();
                String sql = "SELECT * FROM MYDB.USERS";
                try {
                    Statement stmt = connection.createStatement();
                    stmt.executeUpdate("USE MYDB");
                    stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);
                    JTable book_list = new JTable();
                    book_list.setModel(DbUtils.resultSetToTableModel(rs));
                    JScrollPane scrollPane = new JScrollPane(book_list);
                    f.add(scrollPane);
                    f.add(book_list);
                    f.setSize(800,400);
                    f.setVisible(true);
                    f.setLocationRelativeTo(null);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, ex);

                }
            }
        });
        JButton issued_but = new JButton("View issued books");
        issued_but.setBounds(280,20,120,25);
        issued_but.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame f = new JFrame("Issued books");

                Connection connection = connect();
                String sql = "SELECT * FROM MYDB.ISSUED";
                try {
                    Statement stmt = connection.createStatement();
                    stmt.executeUpdate("USE MYDB");
                    stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);
                    JTable book_list = new JTable();
                    book_list.setModel(DbUtils.resultSetToTableModel(rs));
                    JScrollPane scrollPane = new JScrollPane(book_list);

                    f.add(book_list);
                    f.setSize(800,400);
                    f.setVisible(true);
                    f.setLocationRelativeTo(null);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, ex);

                }
            }
        });

        JButton add_user = new JButton("Add user");
        add_user.setBounds(20,60,120,25);
        add_user.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame g = new JFrame("User details");
                JLabel l1,l2;
                l1 = new JLabel("Username");
                l1.setBounds(30,15,100,30);
                l2 = new JLabel("Password");
                l2.setBounds(30,50,100,30);
                JTextField F_user = new JTextField();
                F_user.setBounds(110,15,200,30);
                JPasswordField F_pass = new JPasswordField();
                F_pass.setBounds(110,50,200,30);
                JRadioButton a1 = new JRadioButton("Admin");
                a1.setBounds(55,80,100,30);
                JRadioButton a2 = new JRadioButton("User");
                a2.setBounds(155,80, 100, 30);
                ButtonGroup bg = new ButtonGroup();
                bg.add(a1);
                bg.add(a2);

                JButton create_but = new JButton("Create");
                create_but.setBounds(130,130,80,25);
                create_but.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String username = F_user.getText();
                        String password = F_pass.getText();
                        Boolean admin = false;
                        if (a1.isSelected()){
                            admin = true;
                        }
                        Connection connection = connect();
                        try {
                            Statement stmt = connection.createStatement();
                            stmt.executeUpdate("USE MYDB");
                            stmt.executeUpdate("INSERT INTO USERS(USERNAME,PASSWORD,ADMIN) VALUES ('" + username + "','" + password + "'," + admin + ")");
                            JOptionPane.showMessageDialog(null,"User added!");
                            g.dispose();
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, ex);

                        }
                    }
                });
                g.add(create_but);
                g.add(a1);
                g.add(a2);
                g.add(l1);
                g.add(l2);
                g.add(F_pass);
                g.add(F_user);
                g.setSize(350, 200);
                g.setLayout(null);
                g.setVisible(true);
                g.setLocationRelativeTo(null);

            }
        });

        JButton add_book = new JButton("Add book");
        add_book.setBounds(150,60,120,25);
        add_book.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame g = new JFrame("Book details");
                JLabel l1,l2,l3;
                l1 = new JLabel("Book name");
                l1.setBounds(30,15,100,30);
                l2 = new JLabel("Genre");
                l2.setBounds(30,65,100,30);
                l3 = new JLabel("Price");
                l3.setBounds(30,115,100,30);

                JTextField F_name = new JTextField();
                F_name.setBounds(110,15,200,30);
                JTextField F_genre = new JTextField();
                F_genre.setBounds(110,65,200,30);
                JTextField F_price = new JTextField();
                F_price.setBounds(110,115,200,30);

                JButton create_but = new JButton("Submit");
                create_but.setBounds(130,170,80,25);
                create_but.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String bname = F_name.getText();
                        String genre = F_genre.getText();
                        String price = F_price.getText();
                        int price_int = Integer.parseInt(price);

                        Connection connection = connect();
                        try {
                            Statement stmt = connection.createStatement();
                            stmt.executeUpdate("USE MYDB");
                            stmt.executeUpdate("INSERT INTO BOOKS(BNAME,GENRE,PRICE) VALUES ('" + bname + "','" + genre + "'," + price_int + ")");
                            JOptionPane.showMessageDialog(null,"Book added!");
                            g.dispose();
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, ex);

                        }
                    }
                });
                g.add(create_but);

                g.add(l1);
                g.add(l2);
                g.add(l3);
                g.add(F_name);
                g.add(F_genre);
                g.add(F_price);
                g.setSize(350, 250);
                g.setLayout(null);
                g.setVisible(true);
                g.setLocationRelativeTo(null);

            }
        });

        JButton issue_book = new JButton("Issue book");
        issue_book.setBounds(280,60,120,25);
        issue_book.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame g = new JFrame("Enter details");
                JLabel l1,l2,l3,l4;
                l1 = new JLabel("Book ID");
                l1.setBounds(30,15,100,30);
                l2 = new JLabel("User ID");
                l2.setBounds(30,53,100,30);
                l3 = new JLabel("Period(Days)");
                l3.setBounds(30,90,100,30);
                l4 = new JLabel("Date(D-M-Y)");
                l4.setBounds(30,127,150,30);
                JTextField F_bid = new JTextField();
                F_bid.setBounds(110,15,200,30);
                JTextField F_uid = new JTextField();
                F_uid.setBounds(110,53,200,30);
                JTextField F_period = new JTextField();
                F_period.setBounds(110,90,200,30);
                JTextField F_issue = new JTextField();
                F_issue.setBounds(110,127,200,30);

                JButton create_but = new JButton("Submit");
                create_but.setBounds(130,170,80,25);
                create_but.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String bid = F_bid.getText();
                        String uid = F_uid.getText();
                        String period = F_period.getText();
                        String issue = F_issue.getText();

                        int period_int = Integer.parseInt(period);

                        Connection connection = connect();
                        try {
                            Statement stmt = connection.createStatement();
                            stmt.executeUpdate("USE MYDB");
                            stmt.executeUpdate("INSERT INTO ISSUED(UID,BID,ISSUEDATE,PERIOD) VALUES ('" + uid + "','" + bid + "','" + issue + "'," + period_int + ")");
                            JOptionPane.showMessageDialog(null,"Book issued!");
                            g.dispose();
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, ex);

                        }
                    }
                });
                g.add(create_but);

                g.add(l1);
                g.add(l2);
                g.add(l3);
                g.add(l4);
                g.add(F_uid);
                g.add(F_bid);
                g.add(F_period);
                g.add(F_issue);
                g.setSize(350, 250);
                g.setLayout(null);
                g.setVisible(true);
                g.setLocationRelativeTo(null);

            }
        });

        JButton return_book = new JButton("Return book");
        return_book.setBounds(20,100,380,25);
        return_book.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame g = new JFrame("Enter details");
                JLabel l1,l2;
                l1 = new JLabel("Issue ID");
                l1.setBounds(30,15,100,30);
                l2 = new JLabel("Return date");
                l2.setBounds(30,53,100,30);
                JTextField F_iid = new JTextField();
                F_iid.setBounds(110,15,200,30);
                JTextField F_return = new JTextField();
                F_return.setBounds(110,50,200,30);

                JButton create_but = new JButton("Return");
                create_but.setBounds(130,170,80,25);
                create_but.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String iid = F_iid.getText();
                        String return_date = F_return.getText();

                        Connection connection = connect();
                        try {
                            Statement stmt = connection.createStatement();
                            stmt.executeUpdate("USE MYDB");
                            String date1 = null;
                            String date2 = return_date;

                            ResultSet rs = stmt.executeQuery("SELECT ISSUEDATE FROM ISSUED WHERE IID="+iid);
                            while (rs.next()){
                                date1=rs.getString(1);
                            }
                            int days = 0;
                            try {
                                Date date_1 = new SimpleDateFormat("dd-MM-yyyy").parse(date1);
                                Date date_2 = new SimpleDateFormat("dd-MM-yyyy").parse(date2);
                                long diff = date_2.getTime()-date_1.getTime();
                                days = (int)(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                            }
                            stmt.executeUpdate("UPDATE ISSUED SET RETURNDATE='" + return_date + "' WHERE IID =" + iid);
                            g.dispose();
                            Connection connection1 = connect();
                            Statement stmt1=connection1.createStatement();
                            stmt1.executeUpdate("USE MYDB");
                            ResultSet rs1 = stmt1.executeQuery("SELECT PERIOD FROM ISSUED WHERE IID="+iid);
                            String diff=null;
                            while (rs1.next()){
                                diff = rs1.getString(1);
                            }
                            int diff_int = Integer.parseInt(diff);
                            if (days>diff_int){
                                int fine = (days-diff_int)*10;
                                stmt1.executeUpdate("UPDATE ISSUED SET FINE="+fine+" WHERE IID="+iid);
                                String fine_str=("Fine: UAH "+fine);
                                JOptionPane.showMessageDialog(null,fine_str);
                            }
                            JOptionPane.showMessageDialog(null,"Book returned!");
                            g.dispose();
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, ex);

                        }
                    }
                });
                g.add(create_but);
                g.add(l1);
                g.add(l2);
                g.add(F_iid);
                g.add(F_return);
                g.setSize(350, 250);
                g.setLayout(null);
                g.setVisible(true);
                g.setLocationRelativeTo(null);

            }
        });
        f.add(issue_book);
        f.add(return_book);
        f.add(view_but);
        f.add(users_but);
        f.add(issued_but);
        f.add(add_book);
        f.add(add_user);
        f.setSize(440,200);
        f.setLayout(null);
        f.setVisible(true);
        f.setLocationRelativeTo(null);
    }
}
