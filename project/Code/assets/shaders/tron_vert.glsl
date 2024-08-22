#version 330 core
#define NUMBER_OF_POINT_LIGHTS 5

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texCoord;
layout(location = 2) in vec3 normal;

/* IN */

// Uniforms
uniform mat4 model_matrix;
uniform mat4 view_matrix;
uniform mat4 projection_matrix;

out struct PointLight {
    vec3 position;
    vec3 color;
    vec3 attenuation;
};
uniform PointLight pointLights[NUMBER_OF_POINT_LIGHTS];

// SPOTLIGHT
uniform vec3 spotLightPosition;

uniform vec2 tcMultiplier;

/* OUT */

out struct VertexData {
    vec3 position;
    vec2 texCoord;
    vec3 normal;
} vertexData;

out vec3 toPointLights[NUMBER_OF_POINT_LIGHTS];
out vec3 toCamera;
out vec3 toSpotLight;

void main() {
    vec4 posWorldSpace = model_matrix * vec4(position, 1.0f);
    vec4 posCameraSpace = view_matrix * posWorldSpace;
    gl_Position = projection_matrix * posCameraSpace;

    vertexData.position = posWorldSpace.xyz;
    //Uniform tcMultiplier wird mit der textureCoordinate multipliziert
    vertexData.texCoord = texCoord * tcMultiplier;
    // Transform the normal using the model matrix
    vertexData.normal = (inverse(transpose(view_matrix * model_matrix)) * vec4(normal, 0.0f)).xyz;

    toCamera = -posCameraSpace.xyz;
    toSpotLight = (view_matrix * vec4(spotLightPosition, 1.0)).xyz + toCamera;

    for(int i = 0; i < NUMBER_OF_POINT_LIGHTS; i++) {
        toPointLights[i] = (view_matrix * vec4(pointLights[i].position, 1.0f)).xyz + toCamera;
    }
}