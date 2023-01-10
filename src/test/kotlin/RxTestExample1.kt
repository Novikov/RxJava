import io.reactivex.Observable
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class RxTestExample1 {

    @Test
    fun testBlockingSubscribe(){
        val hitcount = AtomicInteger()
        val source = Observable.interval(1, TimeUnit.SECONDS).take(5)
        source.blockingSubscribe { hitcount.incrementAndGet() }
        assertTrue(hitcount.get() == 5)
    }
}