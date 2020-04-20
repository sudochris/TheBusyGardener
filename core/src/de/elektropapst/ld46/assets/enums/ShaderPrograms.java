package de.elektropapst.ld46.assets.enums;

public enum ShaderPrograms {
    TERRAIN("shader/passthrough.vert", "shader/passthrough.frag");

    public String vertexShaderFile;
    public String fragmentShaderFile;

    ShaderPrograms(String vertexShaderFile, String fragmentShaderFile) {
        this.vertexShaderFile = vertexShaderFile;
        this.fragmentShaderFile = fragmentShaderFile;
    }
}
