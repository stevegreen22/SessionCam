import com.typesafe.scalalogging.{LazyLogging, Logger}
import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest.{FlatSpec, Matchers}
import org.slf4j.{Logger => Underlying}

class Testable extends LazyLogging {
    def foo(int: Int) = {
        logger.info("Foo has been called")
        if(int == 1) throw new IllegalArgumentException("s")
    }
}

import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfterEach
import org.scalatest.junit.JUnitRunner


@RunWith(classOf[JUnitRunner])
class  LoggerTest
  extends FlatSpec with Matchers with BeforeAndAfterEach {


    def initTestable(mocked: Underlying): Testable = {
        new Testable() {
            override lazy val logger = Logger(mocked)
        }
    }


    it should "the mockito stuff" in  {
        val mocked = Mockito.mock(classOf[Underlying])
        when(mocked.isInfoEnabled()).thenReturn(false)

        intercept[IllegalArgumentException] {
            initTestable(mocked).foo(1)
            verify(mocked).info("Foo has been called")
        }

    }
}