#define SNOW_FROST_WEIGHT_SHIFT 3u
#define SNOW_FROST_WEIGHT_MAX 31u

vec4 snow_applyFrost(vec4 color, uint materialParams) {
    uint weight = (materialParams >> SNOW_FROST_WEIGHT_SHIFT) & SNOW_FROST_WEIGHT_MAX;
    if (weight == 0u) {
        return color;
    }

    float frost = u_SnowFrost * float(weight) / float(SNOW_FROST_WEIGHT_MAX);
    return vec4(mix(color.rgb, vec3(color.a), frost), 1.0);
}
