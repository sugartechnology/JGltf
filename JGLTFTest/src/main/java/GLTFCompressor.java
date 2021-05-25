import de.javagl.jgltf.impl.v2.BufferView;
import de.javagl.jgltf.impl.v2.GlTF;
import de.javagl.jgltf.impl.v2.Image;
import de.javagl.jgltf.model.io.GltfWriter;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.ImageOutputStreamImpl;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.zip.DataFormatException;

public class GLTFCompressor {


    public void compressMaterials(List<GLTFMaterial> materials) throws IOException {

        for (GLTFMaterial materisl: materials) {
            if(materisl.diffuse != null)
                materisl.diffuse = compressMap(materisl.diffuse);

            if(materisl.normal != null)
                materisl.normal = compressMap(materisl.normal);

            if(materisl.occlusion != null)
                materisl.occlusion = compressMap(materisl.occlusion);

            if(materisl.metallicRoughness != null)
                materisl.metallicRoughness = compressMap(materisl.metallicRoughness);
        }
    }

    public  GLTFMaterialMap compressMap(GLTFMaterialMap materialMap) throws IOException {

        ByteArrayInputStream inputStream = new ByteArrayInputStream(materialMap.mapData.binaryData);
        BufferedImage buffferdImage =  ImageIO.read(inputStream);
        IIOImage iioImage = new IIOImage(buffferdImage, null , null);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream);

        ImageWriter compressorWriter = ImageIO.getImageWritersByMIMEType( materialMap.map.getMimeType()).next();
        compressorWriter.setOutput(imageOutputStream);

        ImageWriteParam jpgWriteParam = compressorWriter.getDefaultWriteParam();
        jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpgWriteParam.setCompressionQuality(0.05f);

        compressorWriter.write(null, iioImage, jpgWriteParam);
        materialMap.mapData.binaryData = outputStream.toByteArray();

        return materialMap;
    }
}
