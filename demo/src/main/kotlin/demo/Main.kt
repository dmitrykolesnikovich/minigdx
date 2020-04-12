package demo

import com.github.dwursteisen.minigdx.DemoAnimation2
import com.github.dwursteisen.minigdx.DemoPlanet
import com.github.dwursteisen.minigdx.GLConfiguration
import com.github.dwursteisen.minigdx.configuration

@ExperimentalStdlibApi
class Main {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            configuration(
                GLConfiguration(
                    name = "Kotin/JVM",
                    width = 800,
                    height = 800
                )
            ).run {
                val index = args.indexOf("--game")
                when (args.getOrElse(index + 1) { "" }) {
                    "animation" -> DemoAnimation2()
                    else -> DemoPlanet()
                }
            }
        }
    }
}
