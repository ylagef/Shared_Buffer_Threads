import java.util.ArrayList;
import java.util.List;

public class MyProblem {
    public static void main(String[] args) {
        Buffer buffer = new Buffer(5); //Shared buffer with max size.
        Thread producer = new Thread(new Producer(buffer), "Producer");
        Thread consumer = new Thread(new Consumer(buffer), "Consumer");

        producer.start();
        consumer.start();

        try {
            Thread.sleep(5000); //End infinite loop in 5 seconds.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        producer.interrupt();
        consumer.interrupt();

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\nProgram of exercise 4 has terminated."); //Print final message
    }
}

class Producer implements Runnable {
    private final Buffer buffer;

    Producer(Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        int i = 0;
        try {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (buffer) {
                    buffer.write(i++);
                    Thread.sleep(100);
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Producer interrupted.");
        }
    }
}

class Consumer implements Runnable {
    private final Buffer buffer;

    Consumer(Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (buffer) {
                    buffer.read();
                    Thread.sleep(100);
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Consumer interrupted.");
        }
    }
}

class Buffer {
    private int size;
    private List<Integer> list = new ArrayList<>();

    Buffer(int size) {
        this.size = size;
    }

    public void write(int i) {
        while (list.size() == size) {
            System.out.println("Buffer is full.");
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }

        System.out.println("Producer + " + i);
        list.add(i);
        notify();
    }

    public void read() {
        while (list.isEmpty()) {
            System.out.println("Buffer is empty.");
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }

        int r = list.get(0);
        System.out.println("Consumer - " + r);
        list.remove(0);
        notify();
    }
}