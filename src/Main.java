import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
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
                        Statement stmt = connection.createStatement();
                        String st = "SELECT * FROM USERS WHERE USERNAME='"+username+"' AND PASSWORD = '"+password+"'";
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
                                    //admin_menu();
                                }else {
                                    //user_menu(UID);
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
    public static Connection connect(){
            return null;
    }
}
