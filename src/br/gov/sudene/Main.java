package br.gov.sudene;
import java.util.*;
import java.sql.*;
import java.util.logging.Logger;
import org.postgresql.Driver;

public class Main {
    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e){
            System.err.print("Classdriver not initialized or founded.");
        }
        String url = "jdbc:sqltype://host/dbname";
        String user = "user";
        String psswd = "password";
	    String br = "pt-br";
        int ptpt = 0;
        int ptbr = 0;
        int none = 0;
        int touched = 0;
        int untouched = 0;


	    try(Connection cnctt = DriverManager.getConnection(url, user, psswd);
            Statement st = cnctt.createStatement();
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users")){

	        if(rs.next()){

                int rows = Integer.parseInt(rs.getString(1));

                for(int i = 1; i <= rows; i++){
                    ResultSet forRS = st.executeQuery("SELECT preferences FROM users WHERE id=" + i);
                    forRS.next();
                    String oldPref = forRS.getString(1);

                    if((oldPref != null) && oldPref.contains("pt-pt")){
                        String newPref = oldPref.replaceAll("pt-pt", br);
                        String updUser = "UPDATE users SET preferences= ? WHERE id=?";

                        PreparedStatement prepSt2  = cnctt.prepareStatement(updUser);
                        prepSt2.setString(1, newPref);
                        prepSt2.setInt(2, i);

                        prepSt2.executeUpdate();
                        System.out.printf("pt-PT encontrado em: %d.\n", i);
                        ptpt++;
                        touched++;
                    }
                    else if((oldPref != null) && oldPref.contains("pt-br")){ //Do  nothing!
                        System.out.printf("pt-BR encontrado em: %d.\n", i);

                        ptbr++;
                        untouched++;
                    }
                    else{
                        String newPref = oldPref + "locale: " + br;
                        String updUser = "UPDATE users SET preferences= ? WHERE id=?";

                        PreparedStatement prepSt2  = cnctt.prepareStatement(updUser);
                        prepSt2.setString(1, newPref);
                        prepSt2.setInt(2, i);

                        prepSt2.executeUpdate();
                        System.out.printf("%d sem preferences, incluindo o pt-br.\n", i);

                        none++;
                        touched++;
                    }
                }
            }
            System.out.printf("%d usuários com pt-pt atualizados.\n", ptpt);
            System.out.printf("%d usuários com pt-br.\n", ptbr);
            System.out.printf("%d usuários não tocados.\n", untouched);
            System.out.printf("%d usuários sem preferences de linguagem.\n\n", none);
            System.out.printf("Totalizando %d usuários atualizados.", touched);
            cnctt.close();
        } catch(SQLException ex){
            System.err.print("error on query");
        }
    }
}
