
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.locks.ReentrantLock;
import org.example.BerryPicking;

public class BerryPickingTest {

    @Test
    public void testDeadlock() throws InterruptedException {
        BerryPicking.Neighbor n1 = new BerryPicking.Neighbor("N1");
        BerryPicking.Neighbor n2 = new BerryPicking.Neighbor("N2");

        Thread thread1 = new Thread(n1);
        Thread thread2 = new Thread(n2);

        thread1.start();
        thread2.start();

        thread1.join(5000);
        thread2.join(5000);

        // Если оба потока не завершились за 5 секунд, это признак deadlock
        assertTrue(!thread1.isAlive() || !thread2.isAlive(), "Возможен Deadlock: Потоки не завершились за отведенное время");

        if(thread1.isAlive()){
            thread1.interrupt();
        }
        if(thread2.isAlive()){
            thread2.interrupt();
        }
    }

    @Test
    public void testStarvation() throws InterruptedException {
        BerryPicking.setBerries(100);
        BerryPicking.Neighbor n1 = new BerryPicking.Neighbor("N1");
        BerryPicking.Neighbor n2 = new BerryPicking.Neighbor("N2");

        Thread thread1 = new Thread(n1);
        Thread thread2 = new Thread(n2);

        thread1.start();
        thread2.start();

        thread1.join(10000);
        thread2.join(10000);

        assertTrue(!thread1.isAlive() || !thread2.isAlive(), "Потоки застряли");
        if(thread1.isAlive()){
            thread1.interrupt();
        }
        if(thread2.isAlive()){
            thread2.interrupt();
        }
        assertTrue(n1.getProgress() > 0, "Сосед N1 не собрал ни одной ягоды.");
        assertTrue(n2.getProgress() > 0, "Сосед N2 не собрал ни одной ягоды.");
    }

    @Test
    public void testRaceCondition() throws InterruptedException {
        BerryPicking.setBerries(100);
        BerryPicking.Neighbor n1 = new BerryPicking.Neighbor("N1");
        BerryPicking.Neighbor n2 = new BerryPicking.Neighbor("N2");
        Thread thread1 = new Thread(n1);
        Thread thread2 = new Thread(n2);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        int totalPicked = n1.getProgress() + n2.getProgress();
        int finalBerries = BerryPicking.getBerries();
        assertEquals(100, totalPicked + finalBerries, "Общее количество ягод не сходится (возможна гонка данных)");
    }
}
