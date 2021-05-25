import de.javagl.jgltf.impl.v2.*;
import de.javagl.jgltf.model.io.Buffers;
import de.javagl.jgltf.model.io.GltfAsset;
import de.javagl.jgltf.model.io.GltfAssetReader;
import de.javagl.jgltf.model.io.GltfWriter;
import de.javagl.jgltf.model.io.v2.GltfAssetV2;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

public class GLTFParser {

    private GltfAsset asset;
    private GlTF gltf;
    private List<GLTFBinaryBuffer> binaryBuffers;


    public GLTFParser(String uriUrl) throws URISyntaxException, IOException {
        GltfAssetReader gltfAssetReader = new GltfAssetReader();
        asset = gltfAssetReader.read(new URI(uriUrl));
        gltf = (GlTF) asset.getGltf();
    }


    public List<GLTFBinaryBuffer> parseBinary() {

         ByteBuffer binaryData = asset.getBinaryData();
         List<BufferView> bufferViews = gltf.getBufferViews();

        binaryBuffers = new ArrayList<>();
         if(bufferViews == null || bufferViews.size() == 0 )
             return binaryBuffers;

        for (BufferView bufferViewie: bufferViews) {
            GLTFBinaryBuffer bufferView = new GLTFBinaryBuffer();

            bufferView.bufferView = bufferViewie;

            int offset = 0;
            byte[] byteData = new byte[bufferViewie.getByteLength()];
            binaryData.position(bufferViewie.getByteOffset());
            binaryData.get(byteData, offset, bufferViewie.getByteLength());
            bufferView.binaryData =  byteData;

            binaryBuffers.add(bufferView);
        }

        return binaryBuffers;
    }


    byte[] byteArray;
    public void mergeBinary(){

        ByteBuffer byteBuffer = null;
        int offset = 0;


        gltf.getBufferViews().clear();
        for (GLTFBinaryBuffer binaryBuffer: binaryBuffers) {

            gltf.getBufferViews().add(binaryBuffer.bufferView);
            binaryBuffer.bufferView.setByteOffset(offset);
            binaryBuffer.bufferView.setByteLength(binaryBuffer.binaryData.length);
            offset += binaryBuffer.binaryData.length;

            ByteBuffer allocatedBuffer = ByteBuffer.allocate(offset);
            if(byteBuffer != null){
                allocatedBuffer = allocatedBuffer.put(byteBuffer.array());
            }

            allocatedBuffer = allocatedBuffer.put(binaryBuffer.binaryData);
            byteBuffer = allocatedBuffer;
            byteArray = byteBuffer.array();
        }

        byteBuffer.position(0);
        gltf.getBuffers().get(0).setByteLength(byteBuffer.capacity());
        byteArray = byteBuffer.array();
        System.out.println("capacity:" + byteBuffer.capacity());
        System.out.println("length:"+ byteArray.length);

        asset = new GltfAssetV2(gltf, byteBuffer);
        gltf = (GlTF) asset.getGltf();
    }


    public List<GLTFMaterial> getMaterials() throws IOException, DataFormatException, URISyntaxException {

        List<Material> materials = gltf.getMaterials();

        List<GLTFMaterial> resultListGltfMaterial = new ArrayList<>();
        if(materials == null || materials.size() == 0 )
            return resultListGltfMaterial;

        GLTFMaterial gltfMaterialToAdd = null;
        for (Material material : materials) {
            gltfMaterialToAdd = new GLTFMaterial();
            gltfMaterialToAdd.name = material.getName();

            MaterialNormalTextureInfo normalTextureInfo = material.getNormalTexture();
            MaterialPbrMetallicRoughness pbrMetallicRoughness = material.getPbrMetallicRoughness();
            MaterialOcclusionTextureInfo occlusionTextureInfo = material.getOcclusionTexture();

            if (normalTextureInfo != null) {
                int index = normalTextureInfo.getIndex();

                gltfMaterialToAdd.normal = new GLTFMaterialMap();
                gltfMaterialToAdd.normal.name =  material.getName() + "_" + "normal";
                gltfMaterialToAdd.normal.mapData = getImageData(index);
                gltfMaterialToAdd.normal.map = gltf.getImages().get(index);

            }

            if (occlusionTextureInfo != null) {
                int index = occlusionTextureInfo.getIndex();

                gltfMaterialToAdd.occlusion = new GLTFMaterialMap();
                gltfMaterialToAdd.occlusion.name = material.getName() + "_" + "occlusion";
                gltfMaterialToAdd.occlusion.mapData = getImageData(index);
                gltfMaterialToAdd.occlusion.map = gltf.getImages().get(index);
            }

            if (pbrMetallicRoughness != null &&
                    pbrMetallicRoughness.getBaseColorTexture() != null) {

                int index = pbrMetallicRoughness.getBaseColorTexture().getIndex();

                gltfMaterialToAdd.diffuse = new GLTFMaterialMap();
                gltfMaterialToAdd.diffuse.name = material.getName() + "_" + "baseColor";
                gltfMaterialToAdd.diffuse.mapData = getImageData(index);
                gltfMaterialToAdd.diffuse.map = gltf.getImages().get(index);
            }

            if (pbrMetallicRoughness != null &&
                    pbrMetallicRoughness.getMetallicRoughnessTexture() != null) {

                int index = pbrMetallicRoughness.getMetallicRoughnessTexture().getIndex();

                gltfMaterialToAdd.metallicRoughness = new GLTFMaterialMap();
                gltfMaterialToAdd.metallicRoughness.name = material.getName() + "_" + "metallicRougness";
                gltfMaterialToAdd.metallicRoughness.mapData = getImageData(index);
                gltfMaterialToAdd.metallicRoughness.map = gltf.getImages().get(index);
            }
            resultListGltfMaterial.add(gltfMaterialToAdd);
        }
        return  resultListGltfMaterial;
    }


    public GLTFBinaryBuffer getImageData(int index){
        Image image = gltf.getImages().get(index);
        GLTFBinaryBuffer binaryBufferData = binaryBuffers.get(image.getBufferView());
        return binaryBufferData;
    }



    public GltfAsset getAsset(){
        return asset;
    }
}