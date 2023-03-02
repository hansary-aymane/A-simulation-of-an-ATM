import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Client implements Comparable<Client>{
    String cin;
    String nom;
    String prenom;
    String telephone;

    public Client(String c,String n,String p,String t) {
        this.cin=c;
        this.nom=n;
        this.prenom=p;
        this.telephone=t;
    }

    public Client() {

    }

    public int Authentification(int num_carte, int pin) throws LoginErrone{
        int num_compte=0;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/banque_chohada", "root", "");
            String sql1 = "select numero_compte from compte co, carte ca where co.id=ca.compte and ? in (Select numero from carte where compte = co.id) and ? in (Select pin from carte where compte = co.id); ";

            PreparedStatement preparedStmt1 = con.prepareStatement(sql1);
            preparedStmt1.setInt(1, num_carte);
            preparedStmt1.setInt(2, pin);
            ResultSet rs1 = preparedStmt1.executeQuery();
            if (rs1.next()) {
                num_compte = rs1.getInt(1);
                con.close();
            }
            else throw new LoginErrone("Carte incorect");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return num_compte;
    }

    public void consulterBalance(int numero_compte) throws ClientInExistant {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con= DriverManager.getConnection("jdbc:mysql://localhost:3306/banque_chohada" ,"root","");
            String sql = "select balance from compte where numero_compte = ?";
            PreparedStatement preparedStmt = con.prepareStatement(sql);
            preparedStmt.setInt(1, numero_compte);
            ResultSet rs = preparedStmt.executeQuery();
            if (!rs.next()) {
                throw new ClientExistant("Compte n'existe pas");
            }

            PrintStream var10000 = System.out;
            String var10001 = rs.getString(1);
            var10000.println("Balance : " + rs.getInt(1));
            con.close();
        } catch (Exception var7) {
            System.out.println(var7);
        }

    }

    public void RetirerArgent(int argent, int numero, int pin) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/banque_chohada", "root", "");
            String sql2 = "update compte set balance = balance-? where id=(select compte from carte where numero=? AND pin=?) AND ?<=balance;";
            PreparedStatement preparedStmt2 = con.prepareStatement(sql2);
            preparedStmt2.setInt(1, argent);
            preparedStmt2.setInt(2, numero);
            preparedStmt2.setInt(3, pin);
            preparedStmt2.setInt(4, argent);
            preparedStmt2.execute();
            con.close();
        } catch (Exception var7) {
            System.out.println(var7);
        }
    }

    public String toString(){
        return "cin :"+this.cin +"| name :" + this.nom + "| prenom :" + this.prenom + "| telephone :" + this.telephone;
    }

    @Override
    public int compareTo(Client o) {
        return this.cin.compareTo(o.cin);
    }
}