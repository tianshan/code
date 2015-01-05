package tianshan.code;

public class RSCoderProtocol extends CodingMatrix {

	public RSCoderProtocol(byte row, byte column) {
		super();
		
		this.row = row;
		this.column = column;
		this.matrix = initialCauchyMatrix(row, column);
	}

	public byte[][] initialCauchyMatrix(int k, int n) {
		byte[][] G = new byte[k][n];
		short[][] E = new short[k][];

		for (int i = 0; i < k; i++) {
			E[i] = new short[n];

			for (int j = 0; j < k; j++)
				if (i == j)
					E[i][j] = 1;
				else
					E[i][j] = 0;
		}
		for (short j = 0; j < n; j++)
			for (short i = 0; i < k; i++)
				E[i][j] = div[1][(j) ^ (i + n)];

		for (short j = 0; j < n; j++)
			for (short i = 0; i < k; i++)
				G[i][j] = (byte) E[i][j];
		return G;
	}

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

	@Override
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

	@Override
	public int decoder(short[][] g, byte[][] Buf, int[] buflen, int offset,
			byte[] buf) {

		int len = 0;
		int off = offset;
		short[] Input;
		short[] Output;
		int k = g.length;
		
//		String s = "\n";
//		for (int j = 0; j < k; j++) {
//			for (int i = 0; i < k; i++) {
//				s += g[j][i] + " ";
//			}
//			s += "\n";
//		}

		g = getInvertedMatrix(g);

		for (int t = 0; t < k; t++) {
			len = 0;
			for (int j = 0; j < k; j++) {
				if (g[j][t] != 0)
					if (buflen[j] != -1 && len < buflen[j]) {
						len = buflen[j];
					}
			}
			Input = new short[len];
			Output = new short[len];

			for (int i = 0; i < Output.length; i++) {
				Output[i] = (short) 0;
			}

			for (int i = 0; i < k; i++) {
				if (g[i][t] != 0) {
					for (int p = 0; p < buflen[i]; p++) {
						if (Buf[i][p] < 0)
							Input[p] = (short) (Buf[i][p] + 256);
						else
							Input[p] = (short) Buf[i][p];
					}
					for (int j = 0; j < buflen[i]; j++) {
						Output[j] ^= mult[Input[j]][g[i][t]];
					}

				}
			}

			for (int i = 0; i < Output.length; i++) {
				buf[off++] = (byte) Output[i];
			}

		}
		return (off - offset);
	}

}
