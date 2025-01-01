package Demo;// GUI.java
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.BlockingQueue;

public class gui extends JFrame {
    private JTextArea inputBox1;
    private JTextArea inputBox2;
    private JTextArea outPut;
    private BlockingQueue<Message> outputQueue;
    public gui(BlockingQueue<Message> outputQueue) {
        this.outputQueue = outputQueue;
        // 设置框架属性
        setTitle("Reckoner Output");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        // 创建文本区域来显示输出
        inputBox1= new JTextArea();
        inputBox1.setEditable(false);
        JScrollPane input1scrollPane = new JScrollPane(inputBox1);
        input1scrollPane.setBounds(10,10,240,150);
        inputBox2= new JTextArea();
        inputBox2.setEditable(false);
        JScrollPane input2ScrollPane = new JScrollPane(inputBox2);
        input2ScrollPane.setBounds(260,10, 240, 150);
        outPut= new JTextArea();
        outPut.setEditable(false);
        JScrollPane outPutScrollPane = new JScrollPane(outPut);
        outPutScrollPane.setBounds(510, 10, 340, 150);
        JPanel panel = new JPanel();
        panel.setLayout(null); // 自定义布局
        panel.add(input1scrollPane);
        panel.add(input2ScrollPane);
        panel.add(outPutScrollPane);
        setContentPane(panel);
//        add(input1scrollPane);
//        add(input2ScrollPane);
//        add(outPutScrollPane);
        // 启动一个线程来从队列中读取输出并更新文本区域
        new Thread(() -> {
            try {
                Message message;
                while ((message = outputQueue.take()) != null) {
                    // 使用SwingUtilities.invokeLater来确保更新UI的操作在事件调度线程上执行
                    Message m = message;
                    if(message.getType().common(MessageType.Box1Input)){
                        System.out.println("收到Box1");
                        SwingUtilities.invokeLater(() -> inputBox1.append(m.getText() + "\n"));
                    }else if(message.getType().common(MessageType.Box2Input)){
                        System.out.println("收到Box2");
                        SwingUtilities.invokeLater(() -> inputBox2.append(m.getText() + "\n"));
                    }else if(message.getType().common(MessageType.Output)){
                        System.out.println("收到Out");
                        SwingUtilities.invokeLater(() -> outPut.append(m.getText() + "\n"));
                    }
                        //Thread.sleep(500); // 延时1秒（1000毫秒）
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        // 显示框架
        setVisible(true);
    }
}