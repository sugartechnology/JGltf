import de.javagl.jgltf.impl.v2.*;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.io.GltfAsset;
import de.javagl.jgltf.model.io.GltfAssetReader;
import de.javagl.jgltf.model.io.GltfAssetWriter;
import de.javagl.jgltf.model.io.GltfWriter;
import de.javagl.jgltf.model.io.v2.GltfAssetV2;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.zip.DataFormatException;

public class BrowserTest {

    public static void main(String[] args) throws URISyntaxException, IOException, DataFormatException {
        System.out.println("nabersin");

        String uriUrl = "file:///Users/yufae/Downloads/source.glb";
        GLTFParser parser = new GLTFParser(uriUrl);

        parser.parseBinary();

        List<GLTFMaterial> materials = parser.getMaterials();

        GLTFCompressor compressor = new GLTFCompressor();
        compressor.compressMaterials(materials);

        parser.mergeBinary();


        String outputFilePath = "/Users/yufae/Downloads/Right2.glb";
        File outputFile = new File(outputFilePath);
        FileOutputStream outputStream = new FileOutputStream(outputFile);


        GltfAssetWriter gltfAssetWriter = new GltfAssetWriter();
        gltfAssetWriter.writeBinary(parser.getAsset(), outputStream);

    }





}
