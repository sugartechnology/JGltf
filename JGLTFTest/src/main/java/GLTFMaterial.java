import de.javagl.jgltf.impl.v2.BufferView;
import de.javagl.jgltf.impl.v2.Image;

import java.nio.ByteBuffer;

public class GLTFMaterial {
    String name;

    GLTFMaterialMap diffuse;
    GLTFMaterialMap metallicRoughness;
    GLTFMaterialMap normal;
    GLTFMaterialMap occlusion;

}

class GLTFMaterialMap{

    String name;
    Image map;
    GLTFBinaryBuffer mapData;
}


class GLTFBinaryBuffer{

    byte[] binaryData;
    BufferView bufferView;
}
