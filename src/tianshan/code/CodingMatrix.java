package tianshan.code;

import java.util.ArrayList;
import java.util.List;

public abstract class CodingMatrix /*implements Writable*/{
	byte[][] matrix;
	byte row;
	byte column;
	
	private static int NW = (1 << 8);
	private static short[] gflog = new short[NW];
	private static short[] gfilog = new short[NW];
	public static short[][] div = new short[NW][NW];
	public static short[][] mult = new short[NW][NW];
	
	public static final byte XOR = 1;
	public static final byte RS = 2;
	
	// seq RC.1 1
	// added by ds at 2014-4-23
	// RC type
	public static final byte RC = 3;
	
	public CodingMatrix(){
//		row = 0;
//		column = 0;
//		matrix = null;
		
		// initial GF multiply and division tables
		setupTables();
		calculateValue();
	}
	
	public CodingMatrix(CodingMatrix matrix) {
		this.row = matrix.row;
		this.column = matrix.column;
		this.matrix = new byte[row][column];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < column; j++) {
				this.matrix[i][j] = matrix.getElemAt(i, j); 
			}
		}
	}
	
	public CodingMatrix(byte row, byte column) {
		this.row = row;
		this.column = column;
		matrix = new byte[row][column];
	}
	public byte[][] getCodingmatrix(){
		 return matrix;
	}
	public byte getRow(){
		return row;
	}
	public byte getColumn(){
		return column;
	}
	
	//==============================================================
	/**
	 * Galois Filed operation
	 */
	private void setupTables() {
		int b = 1;
		for (int log = 0; log < NW - 1; log++) {
			gflog[b] = (short) log;
			gfilog[log] = (short) b;
			b = (b << 1);
			if ((b & (0x0100)) != 0)
				b = (b ^ (0x011D));
		}
	}

	// GF multiply
	private short multV(int a, int b) {
		int sum_log;
		if (a == 0 || b == 0)
			return 0;
		sum_log = gflog[a] + gflog[b];
		if (sum_log >= (NW - 1))
			sum_log -= (NW - 1);
		return gfilog[sum_log];
	}

	// GF divide
	private short divV(int a, int b) {
		int diff_log;
		if (a == 0)
			return 0;
		if (b == 0)
			return 0;
		diff_log = gflog[a] - gflog[b];
		if (diff_log < 0)
			diff_log += NW - 1;
		return gfilog[diff_log];
	}

	private void calculateValue() {
		for (int i = 0; i < NW; i++)
			for (int j = 0; j < NW; j++) {
				mult[i][j] = multV(i, j);
				div[i][j] = divV(i, j);
			}
	}
	//==============================================================
	
	
//	public List<byte> getRowList(int row){
//		List<byte> s = new ArrayList<byte>();
//		for(int i = 0; i < column; i++)
//			s.add((matrix[row][i]));
//		return s;
//	}
	
	public byte[] getRowArray(int row){
		byte[] s = new byte[column];
		for(int i = 0; i < column; i++)
			s[i] = matrix[row][i];
		return s;
	}
	public int[] getCodingFactorList(int row){
//		int[] cf = new int[column];
		List<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < column; i++){
			if(matrix[row][i] != 0){
				int cf = ((int)matrix[row][i] << 16) + getLastID(i);
				list.add(cf);
			}
		}
		int[] cfl = new int[list.size()];
		for (int i = 0; i < list.size(); i++) {
			cfl[i] = list.get(i);
		}
		return cfl;
	}
	public int getLastID(int column){
		int id = 0;
		for(int i = 0; i < row; i++){
			if(matrix[i][column] != 0){
				id += (1 << i);
			}
		}
		return id;
	}

//	public List<byte[]> Matrix2Serializetion(){
//		List m2sList = new ArrayList<byte[]>();
//		byte[] b = new byte[row];
//		for (int i = 0; i < column; i++) {
//			for (int j = 0; j < row; j++) {
//				b[j] = Matrixat(i ,j);
//			}
//			m2sList.add(b);
//		}
//		
//		return m2sList;
//	}
	public String toString(){
		String s = "Row: " + row + " Column: " + column;
		 for (int i = 0; i < row; i++) {
				s += "\n";
			for (int j = 0; j < column; j++) {
				s += "  " + matrix[i][j];
			}
		}
		return s;
	}
	public byte getElemAt(int i, int j) {
		return matrix[i][j];
	}
	public void setElemAt(int i, int j, byte b) {
		this.matrix[i][j] = b;
	}
	
//	public static byte chooseMatrix(long fileLength){
//		//byte ran = (byte)(Math.random()*2);
//		// seq RC.1 2
//		// modified by ds at 2014-4-23
//		// choose RS or RC
//		// byte ran = CodingMatrix.RS;
//		byte ran = RegeneratingCodeMatrix.isRegeneratingCodeRecovery() ? CodingMatrix.RC : CodingMatrix.RS;
//		return ran;
//	}
	
	public static CodingMatrix getMatrixofCertainType(byte type){
		switch (type) {
		case CodingMatrix.XOR:
			return new XORCoderProtocol();
		case CodingMatrix.RS:
			return new RSCoderProtocol((byte)3, (byte)4);
			// seq RC.1 3
			// added by ds at 2014-4-24
			// case RC
		case CodingMatrix.RC:
			return new RegeneratingCodeMatrix();
			
		default:
			return new RSCoderProtocol((byte)3, (byte)4);
		}
	}
	
	  
//	@Override
//	public void readFields(DataInput input) throws IOException {
//		this.row = input.readByte();
//		this.column = input.readByte();
//		this.matrix = new byte[row][column];
//		for(byte i = 0; i < row; ++i)
//			for(int j = 0; j < column; ++j){
//				matrix[i][j] = input.readByte();
//			}
//	}
//	@Override
//	public void write(DataOutput output) throws IOException {
//		output.writeByte(row);
//		output.writeByte(column);
//		for(byte i = 0; i < row; ++i)
//			for(int j = 0; j < column; ++j){
//				output.writeByte(matrix[i][j]);
//			}
//		
//	}
	
	private void swap(int j, short[][] g, short[][] E) {
		short max = g[j][j];
		int i = -1;
		for (int k = j + 1; k < g.length; k++) {
			if (g[k][j] > max) {
				i = k;
				max = g[k][j];
			}
		}
		if (i != -1) {
			short[] temp;
			temp = E[j];
			E[j] = E[i];
			E[i] = temp;
			temp = g[j];
			g[j] = g[i];
			g[i] = temp;
		}
	}
	
	/**
	 * 矩阵求逆 O(n^3)
	 * @param g
	 * @return
	 */
	public short[][] getInvertedMatrix(short[][] g) {
		short[][] E;
		E = new short[g.length][g.length];

		for (int i = 0; i < g.length; i++) {
			for (int j = 0; j < g.length; j++)
				if (i == j)
					E[i][j] = 1;
				else
					E[i][j] = 0;
		}

		for (int i = 0; i < g.length; i++) {
			swap(i, g, E);
			int k = g[i][i];
			if (k > 1) {
				for (int j = 0; j < g.length; j++) {
					g[i][j] = div[g[i][j]][k];
					E[i][j] = div[E[i][j]][k];
				}
			}
			for (int j = 0; j < g.length; j++) {
				if ((j == i) || (g[j][i] == 0))
					continue;
				k = g[j][i];
				for (int t = 0; t < g.length; t++) {
					g[j][t] = div[g[j][t]][k];
					g[j][t] ^= g[i][t];
					E[j][t] = div[E[j][t]][k];
					E[j][t] ^= E[i][t];
				}
			}
		}
		for (int i = 0; i < g.length; i++) {
			if ((g[i][i] != 1))
				for (int j = 0; j < g.length; j++)
					E[i][j] = div[E[i][j]][g[i][i]];
		}
		return E;

	}
	
	/**
	 * GF multply
	 * @param b1
	 * @param element
	 * @return b1*element
	 */
	public byte mult(byte b1, byte element) {
		short b, key;

		if (b1 < 0)
			b = (short) (b1 + 256);
		else
			b = (short) b1;

		if (element < 0)
			key = (short) (element + 256);
		else
			key = (short) element;
		b = mult[b][key];

		return (byte) b;
	}
	
	/**
	 * GF ?
	 * @param b1
	 * @param b2
	 * @param element
	 * @return b1^(b2*element)
	 */
	public byte code(byte b1, byte b2, byte element) {
		short Buf, inputByte, key;

		if (b2 < 0)
			inputByte = (short) (b2 + 256);
		else
			inputByte = (short) b2;

		if (b1 < 0)
			Buf = (short) (b1 + 256);
		else
			Buf = (short) b1;

		if (element < 0)
			key = (short) (element + 256);
		else
			key = (short) (element);

		Buf ^= mult[inputByte][key];

		return (byte) Buf;
	}
	
	public abstract int decoder(short[][] g,byte[][] Buf,int[] buflen,int offset,byte[] buf);
	
	// seq RC.1 4
	// added by ds at 2014-4-23
	// methods needed in RegeneratingProtol
	// added by ds begins
	public int getStoreFileNodesNum()
	{
		return 0;
	};

	public int getPerNodeBlocksNum()
	{
		return 0;
	};

	public int getRecoveryMinNodesNum()
	{
		return 0;
	};

	public int getRecoveryNodesNum()
	{
		return 0;
	};

	public int getFileCutsNum()
	{
		return 0;
	};

	public byte[][] getVandermondeMatrix()
	{
		return null;
	};

	public int getN()
	{
		return 0;
	};

	public int getA()
	{
		return 0;
	};

	public int getK()
	{
		return 0;
	};

	public int getD()
	{
		return 0;
	};

	public int getB()
	{
		return 0;
	};
	
	
	// added by ds ends
}