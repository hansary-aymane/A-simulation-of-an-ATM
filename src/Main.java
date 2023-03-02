public class Main {
    public static void main(String[] args) {

       /*Employee E= new Employee();
        try
        {
            if(E.Authentification("123")) {
                E.creerCompte("Sh0099","hassan","hassan","0600000000");
                E.creerCompte("BB2098","hansary","aymane","0600000000");
                E.creerCompte("CD08333","belmouden","hachmi","0600000000");
                E.creerCompte("AZ34444","aya","aya","0600000000");
            }
        } catch(Exception e) {System.out.println(e);}
        E.ClientEmpl();
        */

        Client c = new Client();
        try {
            int num = c.Authentification(27,1792);
            c.consulterBalance(num);
        }catch (Exception e){System.out.println(e);}

    }
}