package com.github.dwursteisen.minigdx.ecs.components.particles

import com.github.dwursteisen.minigdx.Percent
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.entities.position
import com.github.dwursteisen.minigdx.math.Vector3

class ParticleGeneration(val emitter: Entity) {

    var particlesIndex: Long = 0L
        internal set
    var particlesTotal: Long = 0L
        internal set

    var particlesIndexPercent: Percent = 0f
        internal set

    var emmiterProgress: Percent = 0f
        internal set
}

class ParticleConfiguration(
    /**
     * Duration of the emission.
     */
    val duration: Seconds,
    /**
     * Number of time the emission will be performed before being stopped
     * If [time] is equal to -1, the emission will loop.
     */
    val time: Long,
    /**
     * Regarding the progress of the emission, return number of particle to emit.
     */
    val emitter: Entity.(Percent) -> Long,
    /**
     * Emit as soon as the Particle Emitter is created.
     */
    val emitOnStartup: Boolean,
    /**
     * Create the particle configuration regarding the progress of the emission
     */
    val particle: (component: ParticleComponent, generation: ParticleGeneration) -> Unit,
    /**
     * Create a particle, regarding the progress of the emission.
     */
    val factory: Entity.(generation: ParticleGeneration) -> Entity,
    /**
     * Update the particle position
     */
    val update: (Seconds, Entity) -> Unit,
) {

    fun toMutable() = MutableParticleConfiguration(
        duration,
        time
    )

    companion object {

        fun spark(
            factory: Entity.(ParticleGeneration) -> Entity,
            numberOfParticles: Long = 4,
            velocity: Float = 1f,
            ttl: Seconds = 1f,
            duration: Seconds = 2f,
            time: Long = 1,
            emitOnStartup: Boolean = false
        ): ParticleConfiguration {
            return ParticleConfiguration(
                emitOnStartup = emitOnStartup,
                duration = duration,
                time = time,
                emitter = {
                    val active = this.get(ParticleEmitterActiveComponent::class)
                    if (active.current.initialized) {
                        0
                    } else {
                        active.current.initialized = true
                        numberOfParticles
                    }
                },
                factory = factory,
                update = { delta, entity ->
                    val particle = entity.get(ParticleComponent::class)
                    entity.position.addLocalTranslation(particle.direction, delta = delta)
                },
                particle = { component, generation ->
                    component.run {

                        val progress = (360f / generation.particlesTotal.toFloat()) * generation.particlesIndex
                        this.direction = Vector3(0f, velocity, 0f)
                            .rotate(0f, 0f, 1f, progress)
                            .rotate(generation.emitter.position.quaternion)

                        this.ttl = ttl
                    }
                }
            )
        }
    }
}

class MutableParticleConfiguration(
    var duration: Seconds,
    var time: Long,
    var initialized: Boolean = false
)
