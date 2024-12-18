package org.example;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BerryPicking {

    private static boolean flag1 = false; // флаг первого соседа
    private static boolean flag2 = false; // флаг второго соседа
    private static final Lock flagLock = new ReentrantLock(); // Замок для защиты флагов
    private static int berries = 100; // Начальное количество ягод
    private static final Random random = new Random(); // Для рандомизации количества собранных ягод

    public static int getBerries() {
        return berries;
    }

    public static void setBerries(int berries) {
        BerryPicking.berries = berries;
    }

    public static class Neighbor implements Runnable {
        private final String name;
        private int progress; // сколько ягод уже собрал

        public Neighbor(String name) {
            this.name = name;
            this.progress = 0;
        }

        @Override
        public void run() { // переопределяем метод run
            while (berries > 0) {
                try {
                    pickBerries();
                } catch (InterruptedException e) { // обязательно проверяем
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println(name + " закончил сбор с " + progress + " ягодами.");
        }

        private void pickBerries() throws InterruptedException {
            flagLock.lock();
            try { //добавляю на всякий случай проверочки
                if (name.equals("N1")) {
                    flag1 = true;
                    while (flag2) {
                        System.out.println("Сосед " + name + " видит флаг N2. Ожидание...");
                        Thread.sleep(random.nextInt(500));
                    }
                } else {
                    flag2 = true;
                    while (flag1) {
                        System.out.println("Сосед " + name + " видит флаг N1. Ожидание...");
                        Thread.sleep(random.nextInt(500));
                    }
                }

                // Проверка на наличие ягод перед входом на поле
                if (berries <= 0) {
                    System.out.println("Сосед " + name + " видит, что ягод больше нет и не заходит на поле :(");
                    return; // Не заходить на поле, если ягод нет
                }

                System.out.println("Сосед " + name + " вошел на поле.");
                int picked = random.nextInt(10) + 1; // Собрать 1-10 ягод
                if (picked > berries) picked = berries;
                berries -= picked;
                progress += picked;
                System.out.printf("Сосед %s собрал %d ягод. Осталось: %d%n", name, picked, berries);
                Thread.sleep(random.nextInt(1000));

            } finally {
                if (name.equals("N1")) flag1 = false;
                else flag2 = false;
                System.out.println("Сосед " + name + " вышел с поля.");
                flagLock.unlock();
            }
        }


    }

    public static void main(String[] args) {
        Neighbor n1 = new Neighbor("N1");
        Neighbor n2 = new Neighbor("N2");

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
    }
}
