#version 330 core
#define NUMBER_OF_POINT_LIGHTS 5

/* IN */

// Input from vertex shader
in struct VertexData {
    vec3 position;
    vec2 texCoord;
    vec3 normal;
} vertexData;

//sampler für einzelne Texturen als uniform
uniform sampler2D diffTex;
uniform sampler2D emitTex;
uniform sampler2D specTex;

uniform float shininess;
uniform vec3 emitColor;

in struct PointLight {
    vec3 position;
    vec3 color;
    vec3 attenuation;
};
uniform PointLight pointLights[NUMBER_OF_POINT_LIGHTS];

// SPOTLIGHT
uniform vec3 spotLightColor;
uniform vec3 spotLightDirection;
uniform float spotLightInnerCone;
uniform float spotLightOuterCone;

in vec3 toPointLights[NUMBER_OF_POINT_LIGHTS];
in vec3 toCamera;
in vec3 toSpotLight;

float compression_factor = 2.7f;

/* OUT */

out vec4 color;

vec3 gamma(vec3 C_linear) {
    return pow(C_linear.rgb, vec3(1.0/compression_factor));
}

vec3 invgamma(vec3 C_gamma) {
    return pow(C_gamma.rgb, vec3(compression_factor));
}

float attenuation(vec3 toLight) {
    //Distanz zu den Lichtquellen erhält man implizit durch die Länge der toLight Vektoren vom Vertexshader
    float distance = length(toLight);
    return 1.0f / (distance * distance);
}

float getCosAngle(vec3 normalizedVector1, vec3 normalizedVector2) {
    return max(0.0f, dot(normalizedVector1, normalizedVector2));
}

float getCosAngleK(float cosAngle) {
    return pow(cosAngle, shininess);
}

//Phong Beleuchtungsfunktion
vec3 brdf(vec3 diffTerm, vec3 specTerm, float cosAlpha, float cosBetaK, vec3 lightColor, vec3 toLight) {
    return (diffTerm * cosAlpha + specTerm * cosBetaK) * lightColor * attenuation(toLight);
}

void main() {

    vec3 diffTerm = invgamma(texture(diffTex, vertexData.texCoord).rgb);
    vec3 specTerm = invgamma(texture(specTex, vertexData.texCoord).rgb);

    vec3 normalizedNormal = normalize(vertexData.normal);
    vec3 normalizedToCamera = normalize(toCamera);

    color = vec4(0.0f);

    // BLOCK 1: Point Lights
    for (int i = 0; i < NUMBER_OF_POINT_LIGHTS; i++) {
        vec3 normalizedToPointLight = normalize(toPointLights[i]);
        vec3 normalizedReflectedToPointLight = reflect(-normalizedToPointLight, normalizedNormal);
        vec3 normalizedHalfwayDirection = normalize(normalizedToPointLight + normalizedToCamera);

        float cosAlpha = getCosAngle(normalizedNormal, normalizedToPointLight);

        float cosBetaK = getCosAngleK(getCosAngle(normalizedReflectedToPointLight, normalizedToCamera));
        float cosBetaK_Halfway = getCosAngleK(getCosAngle(normalizedHalfwayDirection, normalizedNormal));

        color += vec4(brdf(diffTerm, specTerm, cosAlpha, cosBetaK_Halfway, pointLights[i].color, toPointLights[i]), 0.0f);
    }

    // BLOCK 2: Spot Light
    vec3 normalizedToSpotLight = normalize(toSpotLight);
    vec3 normalizedReflectedToSpotLight = reflect(-normalizedToSpotLight, normalizedNormal);
    vec3 normalizedSpotLightDirection = normalize(spotLightDirection);

    float cosTheta = getCosAngle(-normalizedToSpotLight, normalizedSpotLightDirection);
    float cosEpsilon = spotLightInnerCone - spotLightOuterCone;
    float intensity = clamp((cosTheta - spotLightOuterCone) / cosEpsilon, 0.0f, 1.0f);
    float cosAlpha_Spot = getCosAngle(normalizedNormal, normalizedToSpotLight);
    float cosBetaK_Spot = getCosAngleK(getCosAngle(normalizedReflectedToSpotLight, normalizedToCamera));

    color += vec4(brdf(diffTerm, specTerm, cosAlpha_Spot, cosBetaK_Spot, spotLightColor, toSpotLight), 0.0f) * intensity;

    // Gamma "correction"
    color += vec4(invgamma(texture(emitTex, vertexData.texCoord).rgb) * emitColor, 0.0f);
    color.rgb = gamma(color.rgb);
}