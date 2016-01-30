import com.typesafe.scalalogging.{LazyLogging, Logger}
import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest.Matchers
import org.slf4j.{Logger => Underlying}

class Testable extends LazyLogging {
    def foo() = {
        logger.info("Foo has been called")
    }
}

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, FunSuite}


@RunWith(classOf[JUnitRunner])
class  LoggerTest
  extends FunSuite with Matchers with BeforeAndAfterEach {


    def initTestable(mocked: Underlying): Testable = {
        new Testable() {
            override lazy val logger = Logger(mocked)
        }
    }

    test("the mockito stuff") {
        val mocked = Mockito.mock(classOf[Underlying])
        when(mocked.isInfoEnabled()).thenReturn(true)
        initTestable(mocked).foo()
        verify(mocked).info("Foo has been called")
    }
}