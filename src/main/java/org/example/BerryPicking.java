package org.example;
import java.util.concurrent.locks.ReentrantLock;

public class BerryPicking {
    public static final int MAX_BERRIES = 1000;
    public static int currentBerry = MAX_BERRIES;
    private static final ReentrantLock lockField = new ReentrantLock();

    public static class Neighbor implements Runnable {
        private final int id;
        public int berry;

        public Neighbor(int id) {
            this.id = id;
            this.berry = 0;
        }

        @Override
        public void run() {
            while (true) {
                lockField.lock();
                try {
                    if (currentBerry <= 0) {
                        break;
                    }
                    System.out.println(id + ": Пошел на поле собирать ягодки");
                    int pickedBerries = pickBerries();
                    berry += pickedBerries;
                    System.out.println(id + ": Собрал " + pickedBerries + " ягод. Итого: " + berry);
                } finally {
                    lockField.unlock();
                }
            }
        }
    }

    private static int pickBerries() {
        int berriesToPick = (int) (Math.random() * 10 + 1);
        berriesToPick = Math.min(berriesToPick, currentBerry);
        currentBerry -= berriesToPick;
        return berriesToPick;
    }

    public static void main(String[] args) {
        Neighbor n1 = new Neighbor(1);
        Neighbor n2 = new Neighbor(2);

        Thread thread1 = new Thread(n1);
        Thread thread2 = new Thread(n2);

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Все ягоды собраны.");
        System.out.println("N1 собрал: " + n1.berry + " ягод.");
        System.out.println("N2 собрал: " + n2.berry + " ягод.");
    }
}

