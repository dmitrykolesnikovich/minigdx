package demo

import com.curiouscreature.kotlin.math.ortho
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.components.Text
import com.github.dwursteisen.minigdx.ecs.components.UICamera
import com.github.dwursteisen.minigdx.ecs.createFrom
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.entity.text.Font
import com.github.dwursteisen.minigdx.game.GameSystem
import com.github.dwursteisen.minigdx.game.Screen
import com.github.dwursteisen.minigdx.input.Key
import kotlin.math.cos

@ExperimentalStdlibApi
class TextScreen(override val gameContext: GameContext) : Screen {

    private val font: Font by gameContext.fileHandler.get("pt_font")

    lateinit var txt: Entity

    override fun createEntities(engine: Engine) {
        txt = engine.createFrom(font, "Example Of Text", 50f, 50f)

        engine.create {
            val width = gameContext.gl.screen.width
            val height = gameContext.gl.screen.height
            add(
                UICamera(
                    projection = ortho(
                        l = width * -0.5f,
                        r = width * 0.5f,
                        b = height * -0.5f,
                        t = height * 0.5f,
                        n = 0.01f,
                        f = 1f
                    )
                )
            )
            // put the camera in the center of the screen
            add(Position(way = -1f).translate(x = -width * 0.5f, y = -height * 0.5f))
        }
    }

    override fun createSystems(): List<System> {
        return listOf(
            object : System(EntityQuery(Text::class)) {

                var time = 0f
                override fun update(delta: Seconds, entity: Entity) {
                    time += delta

                    entity.get(Position::class).setTranslate(x = cos(time) * 300f)
                }
            }
        )
    }
}

@ExperimentalStdlibApi
class TextGame(gameContext: GameContext) : GameSystem(gameContext, TextScreen(gameContext))
