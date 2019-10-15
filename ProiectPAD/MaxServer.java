import java.net.*;
import java.io.*;
import java.sql.*;

//Clasa principala a aplicatiei
public class MaxServer {

    public static void main(String[] args) throws IOException {

        // server is listening on port 2003
        ServerSocket ss = new ServerSocket(2003);

        // running infinite loop for getting
        // client request
        while (true) {

            Socket s = null;

            try {

                // socket object to receive incoming client requests
                s = ss.accept();

                System.out.println("");
                System.out.println("A new client is connected : " + s);
                System.out.println("Assigning new thread for this client");
                System.out.println("");

                //obtinerea unui flux de intrare convenabil
                DataInputStream in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
                DataOutputStream out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));

                // create a new thread object
                Thread t = new ClientHandler(s, in, out);

                // Invoking the start() method
                t.start();
            } catch (Exception e) {

                s.close();
                e.printStackTrace();

            }

        }
    }
}

class ClientHandler extends Thread {

    DataInputStream in;
    DataOutputStream out;
    Socket s;

    // Constructor
    public ClientHandler(Socket s, DataInputStream in, DataOutputStream out) {
        this.s = s;
        this.in = in;
        this.out = out;
    }

    public static void trimiteDateCatreClient(DataOutputStream out, String sir) throws IOException {
        out.writeUTF(sir); //trimite catre client sirul
        out.flush();//goleste fluxul
        System.out.println(sir);
    }

    public static String primesteDateDeLaClient(DataInputStream in) throws IOException {
        String sir = in.readUTF(); // obtine raspunsul de la client
        //System.out.println("Am primit de la client: " + sir);
        return sir;
    }

    public void run() {
            String sirNumere = "";
            Double nr = 0.0;
            String mesaj = "";
            String whoIs = "";
            try {
                sirNumere = primesteDateDeLaClient(in);
                whoIs = String.valueOf(sirNumere);

                if(whoIs.equals("Pub")){

                    //primirea numarului
                    sirNumere = primesteDateDeLaClient(in);
                    Double tmp = Double.valueOf(sirNumere);
                    nr = tmp.doubleValue();
                    if(nr <=5){
                        //primirea mesajului
                        sirNumere = primesteDateDeLaClient(in);
                        mesaj = String.valueOf(sirNumere);

                        //trimiterea rezultatului
                        trimiteDateCatreClient(out, "Mesajul a fost primit de server. Mesajul : " +  mesaj);

                            try {
                                // create a mysql database connection
                                String myDriver = "org.gjt.mm.mysql.Driver";
                                String myUrl = "jdbc:mysql://localhost/messageChannel";
                                Class.forName(myDriver);
                                Connection conn = DriverManager.getConnection(myUrl, "root", "stayweird");


                                // the mysql insert statement
                                String query = " insert into msg (channel, message)"
                                        + " values (?, ?)";

                                // create the mysql insert preparedstatement
                                PreparedStatement preparedStmt = conn.prepareStatement(query);
                                preparedStmt.setInt(1, (int) Math.round(nr));
                                preparedStmt.setString(2, mesaj);

                                // execute the preparedstatement
                                preparedStmt.execute();

                                conn.close();
                            } catch (Exception e) {
                                System.err.println("Got an exception!");
                                System.err.println(e.getMessage());
                            }
                    }else{
                        sirNumere = primesteDateDeLaClient(in);
                        mesaj = String.valueOf(sirNumere);

                        //trimiterea rezultatului
                        trimiteDateCatreClient(out, "Mesajul a fost primit de server. Mesajul:" + mesaj);

                        try {
                            // create a mysql database connection
                            String myDriver = "org.gjt.mm.mysql.Driver";
                            String myUrl = "jdbc:mysql://localhost/messageChannel";
                            Class.forName(myDriver);
                            Connection conn = DriverManager.getConnection(myUrl, "root", "stayweird");


                            // the mysql insert statement
                            String query = " insert into dead_letter (channel, message)"
                                    + " values (?, ?)";

                            // create the mysql insert preparedstatement
                            PreparedStatement preparedStmt = conn.prepareStatement(query);
                            preparedStmt.setInt(1, (int) Math.round(nr));
                            preparedStmt.setString(2, mesaj);

                            // execute the preparedstatement
                            preparedStmt.execute();

                            conn.close();
                        } catch (Exception e) {
                            System.err.println("Got an exception!");
                            System.err.println(e.getMessage());
                        }



                    }
                }else if(whoIs.equals("Sub")){
                    //primirea numarului
                    sirNumere = primesteDateDeLaClient(in);
                    Double tmp = Double.valueOf(sirNumere);
                    nr = tmp.doubleValue();
                    if(nr <=5) {
                        System.out.println("S-a conectat subscriberul cu tema: " + nr);


                        try {
                            // create a mysql database connection
                            String myDriver = "org.gjt.mm.mysql.Driver";
                            String myUrl = "jdbc:mysql://localhost/messageChannel";
                            Class.forName(myDriver);
                            Connection conn = DriverManager.getConnection(myUrl, "root", "stayweird");
                            Statement stmt=null;

                            stmt = conn.createStatement();
                            String query = "SELECT message FROM msg where channel=" + nr;
                            ResultSet rs=stmt.executeQuery(query);
                            System.out.println("Mesaje: ");

                            //Extact result from ResultSet rs
                            while(rs.next()){
                                //System.out.println(""+rs.getString("message"));
                                trimiteDateCatreClient(out, rs.getString("message") );
                            }
                            // close ResultSet rs
                            rs.close();

                        } catch (Exception e) {
                            System.err.println("Got an exception!");
                            System.err.println(e.getMessage());
                        }

                        //trimiterea rezultatului
                        //trimiteDateCatreClient(out, "Mesajul a fost primit de server");
                    }
                    else {
                        System.out.println("S-a conectat subscriberul cu tema: " + nr);
                        //trimiterea rezultatului
                        trimiteDateCatreClient(out, "Tema inexistenta");
                    }
                }
            } catch (IOException e) {
                //tratarea cazului de exceptie
                System.err.println("Eroare la trimitere/primire date: " + e);
            }
    }
}

