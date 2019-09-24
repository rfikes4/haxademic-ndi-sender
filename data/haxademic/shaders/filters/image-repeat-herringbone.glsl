#ifdef GL_ES
precision highp float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;
uniform vec2 texOffset;
uniform float time = 0.;

uniform sampler2D repeatImg;
uniform float rows = 4.0;
uniform float padding = 0.0;
uniform float aspect = 1.5;
uniform float rotation = 0.;
uniform float offset = 0.5;
uniform float flipY = 0.0;
uniform vec4 baseColor = vec4(1.);


vec2 rotateCoord(vec2 uv, float rads) {
    uv *= mat2(cos(rads), sin(rads), -sin(rads), cos(rads));
		return uv;
}

float map(float value, float low1, float high1, float low2, float high2) {
   return low2 + (value - low1) * (high2 - low2) / (high1 - low1);
}

void main() {
		// processing uv & color
		vec2 uv = vertTexCoord.xy - 0.5;
		uv.x *= texOffset.y / texOffset.x;		// Correct for base aspect ratio
    if(flipY == 1.) uv.y *= -1;
		uv = rotateCoord(uv, rotation);
		uv.x /= aspect;												// apply image repeat aspect ratio

    vec4 origColor = texture2D(texture, vertTexCoord.xy);
		vec4 color = baseColor;

    // calc row index to offset x of every other row
    float rowIndex = floor(uv.y * rows);
    float oddEven = mod(rowIndex, 2.);

    // create grid coords & set color
    vec2 uvRepeat = fract(uv * rows);
    if(oddEven >= 1.) {
        uvRepeat = fract(vec2(offset, 0.) + uv * rows);
    } else {
        uvRepeat = fract(vec2(-offset, 0.) + uv * rows);
    }

    // add padding and only draw once per cell
    float paddingAspect = aspect * padding;
    uvRepeat *= 1. + vec2(paddingAspect, paddingAspect) * 2.;
    uvRepeat -= padding;

    // antialias - probably a very long & stupid way of doing it, but it's smooth :)
    float alphaX = 1.0;
    float alphaY = 1.0;
    float center = 0.5;
    float repeatThresh = 0.499;	// push out a little so we don't cut any texture off. also helps blend nicely when no padding
    float aa = 0.5 - repeatThresh;
    aa *= 0.01;
    float aaX = aa * aspect;
		float aaY = aa / aspect;
    float centerDistX = distance(center, uvRepeat.x);
    float centerDistY = distance(center, uvRepeat.y);
    if(centerDistX > repeatThresh - aaX) alphaX = map(centerDistX, repeatThresh - aaX, repeatThresh + aaX, 1., 0.);
    if(centerDistY > repeatThresh - aaY) alphaY = map(centerDistY, repeatThresh - aaY, repeatThresh + aaY, 1., 0.);
    float alpha = max(alphaX, alphaY);
    color = texture(repeatImg, uvRepeat);
    // vec4 baseColorFinal = (uvRepeat.y <= 0.5) ? vec4(1.) : vec4(0.);
    // color = mix(baseColorFinal, color, alpha);

    // draw repeating texture
    gl_FragColor = vec4(color.r, color.g, color.b, origColor.a);
}
