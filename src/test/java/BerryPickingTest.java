import org.example.BerryPicking;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;


public class BerryPickingTest {

    // Тест на выявление потенциальных дедлоков (взаимных блокировок)
    @Test
    void testForDeadlock() throws InterruptedException {
        // Увеличиваем количество ягод и соседей, чтобы увеличить вероятность дедлока
        BerryPicking.setBerries(1000); // Больше ягод
        int numNeighbors = 5;     // Больше соседей
        ExecutorService executor = Executors.newFixedThreadPool(numNeighbors); // Создаем пул потоков

        // Массив для хранения потоков соседей
        Thread[] threads = new Thread[numNeighbors];

        // Создаем и запускаем соседей
        for (int i = 0; i < numNeighbors; i++) {
            BerryPicking.Neighbor neighbor = new BerryPicking.Neighbor("N" + (i + 1));
            threads[i] = new Thread(neighbor);
            threads[i].start();
        }

        // Ждем завершения всех соседей
        for (Thread thread : threads) {
            thread.join(); // Wait for each thread to finish
        }

        // Проверяем результат
        assertEquals(0, BerryPicking.getBerries(), "Не все ягоды были собраны!");
        executor.shutdown(); // Закрываем пул потоков
        executor.awaitTermination(1, TimeUnit.SECONDS);
    }


    // Тест на выявление гонки данных (race condition)
    @Test
    void testForRaceCondition() throws InterruptedException {
        AtomicInteger totalPicked = new AtomicInteger(0); //Используем AtomicInteger для потокобезопасного подсчета
        int numRepetitions = 2; // Повторяем тест несколько раз, чтобы увеличить вероятность обнаружения гонки
        int numNeighbors = 5; // Количество соседей

        for (int i = 0; i < numRepetitions; i++) { // Повторяем тест несколько раз
            BerryPicking.setBerries(500); // Сбрасываем количество ягод перед каждой итерацией
            ExecutorService executor = Executors.newFixedThreadPool(numNeighbors); // Создаем пул потоков

            // Массив для хранения потоков соседей
            Thread[] threads = new Thread[numNeighbors];

            // Создаем и запускаем соседей
            for (int j = 0; j < numNeighbors; j++) {
                BerryPicking.Neighbor neighbor = new BerryPicking.Neighbor("N" + (j + 1));
                threads[j] = new Thread(neighbor);
                threads[j].start();
            }
            // Ждем завершения всех соседей
            for (Thread thread : threads) {
                thread.join(); // Wait for each thread to finish
            }

            totalPicked.addAndGet(500); //Потокобезопасное добавление

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.SECONDS);
        }

        // Проверка: общее количество собранных ягод должно быть равно начальному количеству ягод
        assertEquals(500 * numRepetitions, totalPicked.get(), "Обнаружена гонка данных: количество собранных ягод не соответствует ожиданиям!");

    }
}
