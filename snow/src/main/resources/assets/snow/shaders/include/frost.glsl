vec4 snow_applyFrost(vec4 color, float progress) {
    uint bits = uint(color.a * 255.0 + 0.5);
    uint weight = bits >> 4u;
    if (weight == 0u || bits == 255u) {
        return color;
    }

    float tint = float((bits & 15u) + 1u) / 16.0;
    float shade = min(max(color.r, max(color.g, color.b)) / tint, 1.0);
    return vec4(mix(color.rgb, vec3(shade), float(weight) / 15.0 * progress), 1.0);
}
