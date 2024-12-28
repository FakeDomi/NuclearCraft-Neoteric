#version 150

uniform sampler2D DiffuseSampler;

in vec2 UV;
out vec4 FragColor;
uniform float Time;
uniform float BlackholeRadius;
uniform float DistortionAmount;

void main() {
    vec2 center = vec2(0.5, 0.5); // Center of the screen
    vec2 offset = UV - center;
    float dist = length(offset);

    if (dist < BlackholeRadius) {
        float distortion = DistortionAmount * (BlackholeRadius - dist) / BlackholeRadius;
        offset *= 1.0 + distortion * sin(Time);
    }

    vec4 color = texture(DiffuseSampler, center + offset);
    FragColor = color;

}
