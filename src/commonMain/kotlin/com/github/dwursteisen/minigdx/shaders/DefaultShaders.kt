package com.github.dwursteisen.minigdx.shaders

object DefaultShaders {

    val MAX_JOINTS = 20

    //language=GLSL
    val simpleVertexShader = """
        #ifdef GL_ES
        precision highp float;
        #endif
        
        uniform mat4 uModelView;
        attribute vec3 aVertexPosition;
        
        void main() {
            gl_Position = uModelView * vec4(aVertexPosition, 1.0);
        }
    """.trimIndent()

    //language=GLSL
    val simpleFragmentShader = """
        #ifdef GL_ES
        precision highp float;
        #endif

        void main() {
              gl_FragColor = vec4(0.0, 0.5, 0.5, 1.0);
        }
    """.trimIndent()

    //language=GLSL
    val vertexShader = """
        #ifdef GL_ES
        precision highp float;
        #endif

        const int MAX_JOINTS = $MAX_JOINTS;
        const int MAX_WEIGHTS = 3;
        
        uniform mat4 uModelMatrix;
        uniform mat4 uViewMatrix;
        uniform mat4 uProjectionMatrix;
        uniform mat4 uNormalMatrix;
        uniform int uArmature;
        
        uniform mat4 uJointTransformationMatrix[MAX_JOINTS];
        
        uniform vec3 uLightAmbient;
        uniform vec3 uLightColor;
        uniform vec3 uLightDirection;
        
        attribute vec3 aVertexPosition;
        attribute vec3 aNormal;
        attribute vec4 aVertexColor;
        attribute vec3 aJoints;
        attribute vec3 aWeights;
        
        varying vec4 vColor;
        varying vec3 vLighting;
        
        void main() {
            vec4 totalLocalPos = vec4(0.0);
            
            if(uArmature > 0) {
                for(int i=0;i<MAX_WEIGHTS;i++){
                    int joinId = int(aJoints[i]);
                    mat4 uJointMatrix = uJointTransformationMatrix[joinId];
                    vec4 posePosition = uJointMatrix * vec4(aVertexPosition, 1.0);
                    totalLocalPos += posePosition * aWeights[i];
                }
            } else {
                totalLocalPos = vec4(aVertexPosition, 1.0);
            }
            
            gl_Position = uProjectionMatrix * uViewMatrix * uModelMatrix * totalLocalPos;
            
            vec4 transformedNormal = uNormalMatrix * uModelMatrix * vec4(aNormal, 1.0);
            
            float directional = max(dot(transformedNormal.xyz, uLightDirection), 0.0);
            
            vLighting = uLightAmbient + (uLightColor * directional);
            vColor = aVertexColor;
        }
    """.trimIndent()

    //language=GLSL
    val fragmentShader = """
        #ifdef GL_ES
        precision highp float;
        #endif

        varying vec4 vColor;
        varying vec3 vLighting;
        
        void main() {
              // see vertex shader
              // gl_FragColor = vColor;
              gl_FragColor = vec4(vColor.rgb * vLighting.rgb, vColor.a);
        }
    """.trimIndent()

    //language=GLSL
    private val vertexShader2d = """
        #ifdef GL_ES
        precision highp float;
        #endif
        
        attribute vec2 aPosition;
        attribute vec2 aTexCoord;

        uniform vec2 uResolution;
        uniform mat4 uViewMatrix;
        uniform mat4 uProjectionMatrix;
        

        varying vec2 vTexCoord;

        void main() {
           // convert the rectangle from pixels to 0.0 to 1.0
           vec2 zeroToOne = aPosition / uResolution;

           // convert from 0->1 to 0->2
           vec2 zeroToTwo = zeroToOne * 2.0;

           // convert from 0->2 to -1->+1 (clipspace)
           vec2 clipSpace = zeroToTwo - 1.0;

           gl_Position = uProjectionMatrix * uViewMatrix * vec4(clipSpace * vec2(1, -1), 0, 1);

           // pass the texCoord to the fragment shader
           // The GPU will interpolate this value between points.
           vTexCoord = aTexCoord;
        }
    """.trimIndent()

    //language=GLSL
    private val fragmentShader2d = """
        #ifdef GL_ES
        precision highp float;
        #endif
        
        // our texture
        uniform sampler2D uTexture;

        // the texCoords passed in from the vertex shader.
        varying vec2 vTexCoord;

        void main() {
           gl_FragColor = texture2D(uTexture, vTexCoord);
        }
    """.trimIndent()

    fun create3d(): ShaderProgramExecutor {
        val program = ShaderUtils.createShaderProgram(vertexShader, fragmentShader)

        program.createAttrib("aVertexPosition")
        program.createAttrib("aVertexColor")
        program.createAttrib("aNormal")
        program.createAttrib("aJoints")
        program.createAttrib("aWeights")

        // Model View Project Matrix
        program.createUniform("uModelMatrix")
        program.createUniform("uViewMatrix")
        program.createUniform("uProjectionMatrix")

        program.createUniform("uNormalMatrix")
        program.createUniform("uArmature")
        program.createUniform("uJointTransformationMatrix")

        // Light
        program.createUniform("uLightAmbient")
        program.createUniform("uLightColor")
        program.createUniform("uLightDirection")

        return ShaderProgramExecutor(program)
    }

    fun create2d(): ShaderProgramExecutor {
        val program = ShaderUtils.createShaderProgram(vertexShader2d, fragmentShader2d)

        program.createAttrib("aPosition")
        program.createAttrib("aTexCoord")

        program.createUniform("uResolution")

        program.createUniform("uViewMatrix")
        program.createUniform("uProjectionMatrix")

        return ShaderProgramExecutor(program)
    }
}
