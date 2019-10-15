import java.net.*;
import java.io.*;


//++++++++++++++++++++++++++++++++++++++++++++++++++++
//S E N D E R
//++++++++++++++++++++++++++++++++++++++++++++++++++++


//Aplicatia client MaxClient
public class Subscriber {
        
        public static void trimiteDateCatreServer(DataOutputStream out, String sir) throws IOException {
            out.writeUTF(sir);//trimite către server sirul
            out.flush();//goleste sirul din flux
            //afisarea unui mesaj in consola
            //System.out.println("Am trimis catre server: " + sir);
        }

        public static String primesteDateDeLaServer(DataInputStream in) throws IOException {
            String sir = in.readUTF();//obtine raspunsul de la server
            //System.out.println("Am primit de la server: " + sir);
            return sir;
        }

        public static void main(String[] args){

            //declararea datelor locale
            DataInputStream in = null;
            DataOutputStream out = null;
            Socket s = null;

            try{
                //stabilirea unei conexiuni cu serverul local de la portul 2003
                s = new Socket("127.0.0.1", 2003);

                //obtinerea unui flux de intrare convenabil
                in = new DataInputStream(new BufferedInputStream(s.getInputStream()));

                //obtinerea unui flux de iesire convenabil
                out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));

            }catch (IOException e) {
                //tratarea exceptiei
                System.err.println("Eroare la conectare: "+ e);
                System.exit(1);
            }

            //declararea altor date auxiliare
            Double nr = 0.0;
            String mesaj = "";
            String whoIs = "Sub";
            BufferedReader tastatura;
            String linie;

            try{
                //obtinerea unui flux de la intrarea standart
                tastatura = new BufferedReader(new InputStreamReader(System.in));
                System.out.flush();

                //citirea primului numar de la tastatura
                System.out.print ("Dati numarul de la 1 - 5: ");
                linie = tastatura.readLine();
                Double tmp = Double.valueOf(linie);
                nr = tmp.doubleValue();
                System.out.flush();

            } catch (IOException e){
                //tratarea cazului de exceptie
                System.err.println("Citire gresita de la tastatura: " + e);
            }

            //comunicarea cu serverul
            String rezultat = "";
            try{
                //trimiterea datelor
                trimiteDateCatreServer(out, whoIs);
                trimiteDateCatreServer(out, Double.toString(nr));

                //primirea rezultatului
                
                rezultat = primesteDateDeLaServer(in);

            }catch (IOException e){
                System.err.println("Eroare la trimitere/primire date: "+ e);
            }
        }
}
