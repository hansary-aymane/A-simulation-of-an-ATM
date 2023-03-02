/*
@author belmoudden el hachmy
*/
import java.sql.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

class ClientExistant extends Exception {
    String message;
    ClientExistant(String message) {
        super(message);
    }
}
class ClientInExistant extends Exception {
    String message;

    ClientInExistant(String message) {
        super(message);
    }
}
class LoginErrone extends Exception {
    String message;
    LoginErrone(String message) {
        super(message);
    }
}
public class Employee {
    private String login;
    private Set<Client> Clients;
    public Employee() {
        Clients=new TreeSet<Client>();
    }
    public boolean Authentification(String L) throws LoginErrone{
        int find = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/banque_chohada", "root", "");
            String sql1 = "select * from employee where login=?;";
            PreparedStatement preparedStmt1 = con.prepareStatement(sql1);
            preparedStmt1.setString(1, L);
            ResultSet rs1 = preparedStmt1.executeQuery();
            if (rs1.next()) {
                find =1;
                con.close();
            }
            else throw new LoginErrone("Employee login incorect");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return (find==1) ? true:false;
    }
    private void addClient(String c,String n,String p,String t){
        Clients.add(new Client(c,n,p,t));
    }
    public void ClientEmpl(){
        for(Client cl : Clients) {
            System.out.println(cl.toString());
        }
    }

    private void creerClient(String c, String n, String p, String t) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/banque_chohada", "root", "");
            String sql1 = "select * from client where cin = ?;";
            PreparedStatement preparedStmt1 = con.prepareStatement(sql1);
            preparedStmt1.setString(1, c);
            ResultSet rs1 = preparedStmt1.executeQuery();
            if (!rs1.next()) {
                String sql = "insert into client(cin,nom,prenom,telephone) values (?,?,?,?)";
                PreparedStatement preparedStmt = con.prepareStatement(sql);
                preparedStmt.setString(1, c);
                preparedStmt.setString(2, n);
                preparedStmt.setString(3, p);
                preparedStmt.setString(4, t);
                preparedStmt.execute();
                con.close();
                System.out.println("client creer avec succes");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private void creerCarteBancaire(int compte) {
        try {
            Random rand = new Random();
            int pin = rand.nextInt(9900) + 10;
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/banque_chohada", "root", "");
            String sql1 = "insert into carte (pin,compte) value(?,?);";
            PreparedStatement preparedStmt1 = con.prepareStatement(sql1);
            preparedStmt1.setInt(1, ThreadLocalRandom.current().nextInt(1000, 3000)); //Generate a Random Code Pin
            preparedStmt1.setInt(2, compte);
            preparedStmt1.execute();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
    public void creerCompte(String c, String n, String p, String t) throws ClientExistant {
        try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/banque_chohada", "root", "");
                String sql1 = "select * from client where cin = ?;";
                PreparedStatement preparedStmt1 = con.prepareStatement(sql1);
                preparedStmt1.setString(1, c);
                ResultSet rs1 = preparedStmt1.executeQuery();
                if (rs1.next()) {
                    throw new ClientExistant("client existe deja");
                }

                this.creerClient(c, n, p, t);
                String sql2 = "select id from client where cin = ?;";
                PreparedStatement preparedStmt2 = con.prepareStatement(sql2);
                preparedStmt2.setString(1, c);
                ResultSet rs2 = preparedStmt1.executeQuery();
                String sql3;
                PreparedStatement preparedStmt3;

                if (rs2.next()) {
                    sql3 = "insert into compte (balance,numero_compte,client,agence) values(?,?,?,?)";
                    preparedStmt3 = con.prepareStatement(sql3);
                    preparedStmt3.setInt(1, 0);
                    preparedStmt3.setInt(2, ThreadLocalRandom.current().nextInt(1000000000,2000000000 )); //Generate a Random Numero Compte
                    preparedStmt3.setInt(3, rs2.getInt(5));
                    preparedStmt3.setInt(4, 1);
                    preparedStmt3.execute();
                }

                sql3 = "select id from compte where client = (select id from client where cin = ?); ";
                preparedStmt3 = con.prepareStatement(sql3);
                preparedStmt3.setString(1, c);
                ResultSet rs3 = preparedStmt3.executeQuery();
                if (rs3.next()) {
                    this.creerCarteBancaire(rs3.getInt(1));
                }
                con.close();
                addClient(c, n, p, t);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
    public void consulterInfo(String cin) throws ClientInExistant {
        try {

                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/banque_chohada", "root", "");
                String sql = "select c.cin,c.nom,c.prenom,c.telephone,p.balance,p.numero_compte from client c,compte p where c.id=p.client and c.cin=?; ";
                PreparedStatement preparedStmt = con.prepareStatement(sql);
                preparedStmt.setString(1, cin);
                ResultSet rs = preparedStmt.executeQuery();
                if (!rs.next()) {
                    throw new ClientExistant("client n'existe pas");
                }

                System.out.println(" cin : " + rs.getString(1) + " | prenom : " + rs.getString(2) + " | nom : " + rs.getString(3) + " telephone : " + rs.getString(4) + " balance : " + rs.getInt(5) + " numero de compte : " + rs.getString(6));
                con.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
    public void DeposerArgent(int argent, String cin)throws ClientInExistant {
        if(argent>0) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/banque_chohada", "root", "");
                String sql1 = "select * from client where cin = ?;";
                PreparedStatement preparedStmt1 = con.prepareStatement(sql1);
                preparedStmt1.setString(1, cin);
                ResultSet rs1 = preparedStmt1.executeQuery();
                if (rs1.next()) {
                    String sql2 = "update compte set balance = balance+? where client = (select id from client where cin=?);";
                    PreparedStatement preparedStmt2 = con.prepareStatement(sql2);
                    preparedStmt2.setInt(1, argent);
                    preparedStmt2.setString(2, cin);
                    preparedStmt2.execute();
                    System.out.println("client supprimer avec succes");
                }
                else throw new ClientInExistant("client n\'existe pas" );
                con.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }else System.out.println("vous voulez deposer argent ???? argent>>>0");

    }
    public void SupprimerClient(String c) throws ClientInExistant{

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/banque_chohada", "root", "");
            String sql1 = "select * from client where cin = ?;";
            PreparedStatement preparedStmt1 = con.prepareStatement(sql1);
            preparedStmt1.setString(1, c);
            ResultSet rs1 = preparedStmt1.executeQuery();
            if (rs1.next())
            {
                String sql = "delete from client where cin=?;";
                PreparedStatement preparedStmt2 = con.prepareStatement(sql);
                preparedStmt2.setString(1, c);
                preparedStmt2.execute();
                System.out.println("client supprimer avec succes");
            }
            else throw new ClientInExistant("client n\'existe pas" );
            con.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}