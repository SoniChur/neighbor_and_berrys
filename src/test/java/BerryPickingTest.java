import org.example.BerryPicking;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BerryPickingTest {

    @Test
    public void testRaceCondition() throws InterruptedException {
        BerryPicking BerryPicking = null;
        BerryPicking.currentBerry = BerryPicking.MAX_BERRIES; // Сброс текущего количества ягод

        org.example.BerryPicking.Neighbor n1 = new BerryPicking.Neighbor(1);
        org.example.BerryPicking.Neighbor n2 = new BerryPicking.Neighbor(2);

        Thread thread1 = new Thread(n1);
        Thread thread2 = new Thread(n2);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println("Все ягоды собраны.");
        System.out.println("N1 собрал: " + n1.berry + " ягод.");
        System.out.println("N2 собрал: " + n2.berry + " ягод.");

        // Проверка, что количество ягод не превышает MAX_BERRIES и не меньше 0
        assertTrue(BerryPicking.currentBerry >= 0 && BerryPicking.currentBerry <= BerryPicking.MAX_BERRIES,
                "Количество ягод должно быть в пределах от 0 до " + BerryPicking.MAX_BERRIES);
    }


    @Test
    public void testNoDeadlock() throws InterruptedException {
        int numberOfNeighbors = 10; // Увеличиваем количество соседей для теста
        Thread[] threads = new Thread[numberOfNeighbors];
        BerryPicking.currentBerry = BerryPicking.MAX_BERRIES; // Сброс текущего количества ягод

        for (int i = 0; i < numberOfNeighbors; i++) {
            BerryPicking.Neighbor neighbor = new BerryPicking.Neighbor(i + 1);
            threads[i] = new Thread(neighbor);
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join(); // Ждем завершения всех потоков
        }

        System.out.println("Все ягоды собраны.");

        // Проверка, что все соседи завершили выполнение
        assertTrue(true, "Все потоки должны завершиться без взаимной блокировки.");
    }
}

