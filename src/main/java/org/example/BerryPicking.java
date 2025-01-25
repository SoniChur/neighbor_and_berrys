package org.example;

import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class BerryPicking {

    private static volatile boolean flag1 = false;
    private static volatile boolean flag2 = false;
    private static final ReentrantLock flagLock = new ReentrantLock(true); // Справедливый ReentrantLock
    private static int berries = 100;
    private static final Random random = new Random();

    public static int getBerries() {
        return berries;
    }

    public static void setBerries(int berries) {
        BerryPicking.berries = berries;
    }

    public static class Neighbor implements Runnable {
        private final String name;
        private int progress;

        public Neighbor(String name) {
            this.name = name;
            this.progress = 0;
        }

        public int getProgress() {
            return progress;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    if (berries <= 0) break; // Завершение, если ягоды закончились

                    tryEnterField();
                    pickBerries();
                    exitField();
                    // Thread.sleep(random.nextInt(100)); // маленькая задержка после сбора
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            System.out.println(name + " закончил сбор с " + progress + " ягодами.");
        }

        private void tryEnterField() throws InterruptedException {
            while (true) {
                flagLock.lock();
                try {
                    if ((name.equals("N1") && !flag2) || (name.equals("N2") && !flag1)) {
                        if (name.equals("N1")) {
                            flag1 = true;
                        } else {
                            flag2 = true;
                        }
                        System.out.println("Сосед " + name + " поднимает флаг.");
                        break;
                    } else {
                        System.out.println("Сосед " + name + " видит флаг " + (name.equals("N1") ? "N2" : "N1") + ". Ожидание...");
                    }
                } finally {
                    flagLock.unlock();
                }
            }
        }


        private void exitField() {
            flagLock.lock();
            try {
                if (name.equals("N1")) {
                    flag1 = false;
                } else {
                    flag2 = false;
                }
                System.out.println("Сосед " + name + " спустил флаг.");
            } finally {
                flagLock.unlock();
            }
        }

        // НЕСИНХРОНИЗИРОВАННАЯ СЕКЦИЯ (сбор ягод)
        private void pickBerries() throws InterruptedException {
            System.out.println("Сосед " + name + " вошел на поле.");
            if (berries <= 0)
                System.out.println(name + ": - Ягод не осталось, пойду домой пить чай :(");
            else{

                int picked = random.nextInt(10) + 1; // Случайное количество ягод от 1 до 10
                if (picked > berries) picked = berries; // Если ягод меньше, чем собирать, берем все оставшиеся
                berries -= picked; // Уменьшаем общее количество ягод
                progress += picked; // Увеличиваем прогресс соседа
                System.out.printf("Сосед %s собрал %d ягод. Осталось: %d%n", name, picked, berries);
                // Thread.sleep(random.nextInt(1000)); // Задержка для имитации времени сбора
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Neighbor n1 = new Neighbor("N1");
        Neighbor n2 = new Neighbor("N2");

        Thread thread1 = new Thread(n1);
        Thread thread2 = new Thread(n2);

        thread1.start();
        thread2.start();

        thread1.join(); // Ожидаем завершения первого потока
        thread2.join(); // Ожидаем завершения второго потока

        System.out.println("Сбор ягод завершен.");
        System.out.println("Всего ягод собрано: " + (100 - berries));
        System.out.println("Осталось ягод: " + berries);
    }
}
