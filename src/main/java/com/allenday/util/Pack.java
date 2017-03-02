import java.nio.*;
import java.net.*;
import java.util.*;
import java.lang.*;

class Pack
{   
    public static void main(String [] args)
    {
	// 5 bins, 5 bits, 7 dimensions = 175
	// 8 bins, 4 bits, 7 dimensions (32 reserved bits) = 
        int[] data = {
		1, 2, 3, 4, 5, 6, 7, 0, //R
		1, 2, 3, 4, 5, 6, 7, 0, //G
		1, 2, 3, 4, 5, 6, 7, 0, //B
		1, 2, 3, 4, 5, 6, 7, 0, //T
		1, 2, 3, 4, 5, 6, 7, 0, //C
		1, 2, 3, 4, 5, 6, 7, 0, //M1
		1, 2, 3, 4, 5, 6, 7, 0, //M2
	};
        ByteBuffer byteBuffer = ByteBuffer.allocate(data.length);
	BitSet bs = new BitSet(256);

	String tt = "";
        for (int i=0; i < 56; i++) {
		int low = data[i] >> 32;
		for (int j=0; j<4; j++) {
			if ( (low >> j & 0x1) != 0x0 ) {
				bs.set( i*8+j );
				tt += "1";
			}
			else {
				tt += "0";
			}
//			tt += " ";
		}
//		tt += "\n";
	}
	byte[] array = new byte[56];


int m = 0;
int n = 0;
while(m+8 <= tt.length()){
    array[n] = Byte.valueOf(tt.substring(m, m+8), 2);
    m+=8;
    n++;
}
//	System.err.println(bs.toString());
//	System.err.println(tt);

        for (int i=0; i < data.length; i++) {
	    int low = data[i] >> 32;
            byteBuffer.put( (byte) low );
	}

//        byte[] array = byteBuffer.array();
	byte[] encoded = Base64.getEncoder().encode(array);

        for (int i=0; i < array.length; i++)
        {
	    
//            System.out.println(i + ": " + array[i]);
        }
	System.out.println(new String(encoded));
    }
}
