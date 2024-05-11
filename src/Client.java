import javax.imageio.IIOException;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    public Client(Socket socket, String username){
        try {
            this.socket=socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username=username;
        }catch (IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }
    public void sendMessage(){ // gui cho client mot ten nguoi dung
try {
    bufferedWriter.write(username);
    bufferedWriter.newLine();
    bufferedWriter.flush();

    Scanner scanner= new Scanner(System.in);
    while (socket.isConnected()){
        String messageTosend = scanner.nextLine();
        bufferedWriter.write(username+ ": "+ messageTosend);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }
} catch (IOException e){
    closeEverything(socket,bufferedReader,bufferedWriter); // phuong thuc nhan tin nhan tu may chu
}
    }
    // cac tin nhan duoc nhan tu nguoi dung se duoc luu o sever
    public void listenForMessage(){ // tao luong moi de co the chay voi 1 object or 1 class
new Thread(new Runnable() {
    @Override
    public void run() {
      String messageFromGroupChat;
      while (socket.isConnected()) {
          // nếu vẫn kết nối được máy chủ thì ta sẽ đọc từ buferreader
          while (socket.isConnected()){
              try {
                  messageFromGroupChat = bufferedReader.readLine();
                  System.out.println(messageFromGroupChat);
              }catch (IOException e){
                  closeEverything(socket,bufferedReader,bufferedWriter);
              }
          }
      }
    }
}).start();
    }
    public void closeEverything(Socket socket,BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        try {
            if (bufferedReader!=null){ // kiem tra moi thu xem co null ko :>
                bufferedReader.close();
            }
            if (bufferedWriter !=null){
                bufferedWriter.close();
            }
            if (socket!=null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your username for the group chat : ");
        String username = scanner.nextLine(); // tao ket noi toi sever
        Socket socket = new Socket("localhost",1234);
        Client client = new Client(socket,username);
        client.listenForMessage();
        client.sendMessage();
    }
}
