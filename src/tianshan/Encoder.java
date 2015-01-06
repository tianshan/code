package tianshan;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import tianshan.code.CodingMatrix;
import tianshan.code.RSCoderProtocol;

public class Encoder {
	
	String fileName;
	int k, n;
	
	/**
	 * 编码矩阵
	 */
	CodingMatrix matrix;
	private InputStream is;
	
	public Encoder(String fileName, int k, int n) {
		this.fileName = fileName;
		this.k = k;
		this.n = n;
		matrix = new RSCoderProtocol((byte)k, (byte)n);
		
	}
	
	public void encode() throws IOException {
		byte[] codeBuf;
		
		is = new FileInputStream(fileName);
		OutputStream[] os = new FileOutputStream[n];
		for (int i=0; i<n; i++) {
			os[i] = new FileOutputStream(fileName+".c"+i);
		}
		
		byte[] buf = new byte[k];
		int bytesRead = is.read(buf);
		while (bytesRead>=0) {
			codeBuf = new byte[n];
			for (int i=0; i<k; i++) {
				codeBuf[i] = buf[i];
			}
			for (int i=0; i<n-k; i++) {
				for (int j=0; j<k; j++) {
					codeBuf[i+k] = matrix.code(codeBuf[i+k], buf[j], matrix.getElemAt(j, i));
				}
			}
			
			for (int i=0; i<n; i++) {
				os[i].write(codeBuf[i]);
			}
			
			buf = new byte[k];
			bytesRead = is.read(buf);
		}
				
		for (int i=0; i<n; i++) {
			os[i].close();
		}
	}
	
	public static void main(String[] args) {
		if (args.length<1) {
			System.out.println("Usage: Encoder <fileName>");
			return ;
		}
		String fileName = args[0];
		Encoder en = new Encoder(fileName, 3, 4);
		try {
			long start = System.currentTimeMillis();
			en.encode();
			System.out.println("Time: "+(System.currentTimeMillis()-start)/1000+"s");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
