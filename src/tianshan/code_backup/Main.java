package tianshan.code_backup;

public class Main {
	public static void main(String[] main) {
		int times = 100;
		RSCoderProtocol rs = new RSCoderProtocol();
		short[][] g = rs.initialCauchyMatrix(3, 4);
		long start = System.currentTimeMillis();
		
		for (int i=0; i<times; i++) {
			
			g = rs.initialInvertedCauchyMatrix(g);
		}
		System.out.println( "time: "+(System.currentTimeMillis()-start) );
		
		
	}
}
