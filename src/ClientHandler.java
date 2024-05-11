//Project code by Huy Vo 12/5/2024 Chat application
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
public static ArrayList<ClientHandler>clientHandlers = new ArrayList<>(); //su dung phuong thuc static vi toi muon nothuoc ve class chu khong phai tung object cua class
    // su dung arraylist de theo doi  tat ca ClientHandler de khi nao ma ClientHandler thi sever co the duyet qua va gui tin nhan den tung khach hang
    private Socket socket;
    private BufferedReader bufferedReader; // su dung de doc tin nhan dc gui di tu ClientHandler
    private BufferedWriter bufferedWriter; // ghi vao trong bo nho dem tin nhan  ! tu cac client khac den sever va tu client den client khac
    private String clientUserName;

    public ClientHandler(Socket socket){
        try {
            this.socket=socket; // moi client se duoc 1 Thread rieng
         this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()) )  ;// Chú thích của Huy(cho người sau đọc code) : Trong java có 2 loại luồng là byte và char nhưng vì đây là ứng dụng tin nhắn nên ở đây chúng ta sẽ sử dụng luồng byte
            this.bufferedReader= new BufferedReader(new InputStreamReader(socket.getInputStream())); // tương tự như dòng trên nhưng là output
            this.clientUserName=bufferedReader.readLine();
            clientHandlers.add(this); // Dùng để add thêm client(0369740701 : Author[Huy Võ])
            broadcastMessage("Sever: "+ clientUserName+ " has been entered the chat ! ");
        } catch (IOException e){
            closeEverything(socket,bufferedWriter,bufferedReader);// sử lí ngoại lệ (đóng socket , chuyển vào trình đọc,và trình writer)
        }
    }

    @Override
    public void run() {  // đoạn này chạy trn 1 thread riêng nha
//lí do có 1 thread phụ này (vì khi nhiều client cùng nhắn thì khả năng cao các luồng sẽ bị kẹt khi chờ phản hồi từ client nên sẽ sẽ thread này làm 1 luồng để chờ )
        String messageFromClient;
        while (socket.isConnected())
        {
            try {
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);

            }catch (IOException e){
                closeEverything(socket,bufferedWriter,bufferedReader);
                break;
            }
        }
    }
    public void  broadcastMessage(String messageToSent){
        for (ClientHandler clientHandler : clientHandlers){// mỗi object sẽ sử lý client tuần tự
            try {
                if (!clientHandler.clientUserName.equals(clientUserName)){ //nếu mà người dùng  không có tên bằng với client thì  sẽ gửi tin nhắn đến người đó
                 clientHandler.bufferedWriter.write(messageToSent);
                 clientHandler.bufferedWriter.newLine(); // vì trên máy khách họ sẽ đợi 1 tin nhắn mới  nên sử dụng writer để gửi 1 dòng k tự mới
                 clientHandler.bufferedWriter.flush();// Khi mà nhấn gửi thì sẽ không cần phải đợi nữa
                    // dữ liệu từ bộ nhớ đệm sẽ không được gửi vào luồng "đầu ra" trừ khi nó bị đầy
                    // mà tin nhắn khi gửi thì khả năng rất cao là méo đủ để lấp đầy được bộ nhớ đêm so chúng ta sẽ xóa chay :)))
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedWriter, bufferedReader);
            }
        }
    }
    public void removeClientHandler(){
        clientHandlers.remove(this);
        broadcastMessage("Sever : "+clientUserName+" has left the chat !");
    }
    public void closeEverything(Socket socket,BufferedWriter bufferedWriter,BufferedReader bufferedReader){ // khi mà người dùng rời đi hoặc xảy ra thôi thì cho họ bay màu khỏi group chat
removeClientHandler();
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
}
