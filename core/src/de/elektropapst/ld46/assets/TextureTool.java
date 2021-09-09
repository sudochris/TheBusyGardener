// This file is here for a reason. The texture tool should be refactored!
//package de.elektropapst.ld46.assets;
//
//import com.badlogic.gdx.files.FileHandle;
//import com.badlogic.gdx.graphics.g2d.TextureAtlas;
//import com.badlogic.gdx.tools.texturepacker.TexturePacker;
//
//import java.io.FileNotFoundException;
//import java.io.PrintWriter;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//public class TextureTool {
//
//    public static void main(String[]args) throws FileNotFoundException {
//        TexturePacker.process("raw", "sprites", "packed");
//        generateKeyFile();
//    }
//
//    private static void generateKeyFile() throws FileNotFoundException {
//        TextureAtlas.TextureAtlasData textureAtlasData = new TextureAtlas.TextureAtlasData(new FileHandle("sprites/packed.atlas"), new FileHandle("sprites/packed.atlas").parent(), false);
//        String outputFileContent = "";
//        Path path = Paths.get("..", "src", "de", "elektropapst", "ld46", "assets", "enums", "Textures.java");
//        PrintWriter writer = new PrintWriter(path.toFile());
//        writer.println("/* THIS FILE IS GENERATED. DO NOT MODIFY! */");
//        writer.println("package de.elektropapst.ld46.assets.enums;");
//        writer.println("public enum Textures {");
//        for(TextureAtlas.TextureAtlasData.Region region : textureAtlasData.getRegions()){
//            if(region.index == 0){
//                String line = region.name.toUpperCase().replace('/','_').replace('-','_') + "_LIST(\"" + region.name + "\"),";
//                outputFileContent += line+System.lineSeparator();
//                writer.println(line);
//            } else if(region.index == -1) {
//                String line = region.name.toUpperCase().replace('/','_').replace('-','_') + "(\"" + region.name + "\"),";
//                outputFileContent += line+System.lineSeparator();
//                writer.println(line);
//            }
//        }
//        writer.println(";");
//        writer.println("public String fileName;");
//        writer.println("Textures(String fileName) { this.fileName = fileName; }");
//        writer.println("}");
//
//        writer.flush();
//        writer.close();
//        System.out.println(outputFileContent);
//    }
//}
