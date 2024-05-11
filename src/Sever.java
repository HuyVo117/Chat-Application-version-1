import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Sever {
   private ServerSocket severSocket;

    public Sever(ServerSocket severSocket) {
        this.severSocket = severSocket;
    }
    public void startSever(){
        try{
            while (!severSocket.isClosed()){


              Socket socket= severSocket.accept();
              System.out.println("A new client has connected");
              ClientHandler clientHandler = new ClientHandler(socket);

              Thread thread = new Thread(clientHandler);
              thread.start();
            }
        }catch (IOException e){

        }
    }
    public void closeSeverSocket(){
        try {
            while (severSocket!= null){
                severSocket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException  {
        ServerSocket serverSocket = new ServerSocket(1234);
        Sever sever = new Sever(serverSocket);
        sever.startSever();
    }
}
