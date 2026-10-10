#version 330

in vec3 Position;
in vec2 UV0;

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>
#moj_import <minecraft:globals.glsl>

out vec2 lensUv;
out vec2 shimmerUv;
out float vertexDistance;

void main() {
    vec4 position = ModelViewMat * vec4(Position, 1.0);
    gl_Position = ProjMat * position;
    vertexDistance = length(position.xyz);
    lensUv = UV0;
    shimmerUv = UV0 / 8.0 + vec2(GameTime * 16.0, GameTime * 8.0);
}
