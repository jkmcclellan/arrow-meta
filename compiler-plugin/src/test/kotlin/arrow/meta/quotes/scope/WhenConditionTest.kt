package arrow.meta.quotes.scope

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.WhenConditionPlugin
import org.junit.Test

class WhenConditionTest {

  private val whenCondition = """
                         | //metadebug
                         | 
                         | class Wrapper {
                         |   fun doMaths(x: Int) {
                         |     when {
                         |       x + 2 == 4 -> println("I can do maths")
                         |       else -> println("I cannot do maths")
                         |     }
                         |   }
                         | }
                         | """.trimMargin().source

  @Test
  fun `Validate when condition scope properties`() {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(WhenConditionPlugin())) },
      code = { whenCondition },
      assert = { quoteOutputMatches(whenCondition) }
    ))
  }
}
