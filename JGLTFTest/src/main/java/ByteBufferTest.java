import java.nio.ByteBuffer;

public class ByteBufferTest {

    public  static  void  main(String[] args){
        ByteBuffer byteBuffer = ByteBuffer.allocate(19);
        byteBuffer.limit(2);
        byteBuffer.limit();
        System.out.println("naber");
    }
}
