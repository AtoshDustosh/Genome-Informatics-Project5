package toolkit;

import java.util.HashSet;
import java.util.Set;

public class SmithWaterman {

  public static final int MATCH = 5;
  public static final int MISMATCH = -4;
  public static final int INDEL = -7;

  private String read = "";
  private String ref = "";

  private int resultRow = 0;
  private int resultCol = 0;
  private String cigar = "";
  private int maxScore = 0;

  public SmithWaterman(String read, String ref) {
    this.read = read;
    this.ref = ref;
  }

  public static void main(String[] args) {
    String read = "tcccagttatgtcaggggacacgagcatgcagagac";
    String ref = "aattgccgccgtcgttttcagcagttatgtcagatc";
    SmithWaterman sw = new SmithWaterman(read, ref);
    sw.execute(true);
    System.out.println("cigar: " + sw.getCigar());
  }

  public int getResultRow() {
    return this.resultRow;
  }

  public int getResultCol() {
    return this.resultCol;
  }

  public String getCigar() {
    return this.cigar;
  }

  public int getmaxScore() {
    return this.maxScore;
  }

  public String getRead() {
    return this.read;
  }

  public String getRef() {
    return this.ref;
  }

  public void execute(boolean ifPrintMat) {
    final int rowCount = this.read.length() + 1;
    final int colCount = this.ref.length() + 1;
    // calculate the scoring matrix
    int[][] mat = new int[rowCount][colCount];

    for (int i = 1; i < rowCount; i++) {
      for (int j = 1; j < colCount; j++) {
        char readChar = this.read.charAt(i - 1);
        char refChar = this.ref.charAt(j - 1);
        int matchScore = -1;
        int mismatchScore = -1;
        int indelScore = -1;
        if (readChar == refChar) {
          matchScore = mat[i - 1][j - 1] + MATCH;
        } else {
          mismatchScore = mat[i - 1][j - 1] + MISMATCH;
          indelScore = Math.max(mat[i - 1][j] + INDEL, mat[i][j - 1] + INDEL);
        }
        int newScore = this.max(matchScore, mismatchScore, indelScore);
        if (newScore < 0) {
          newScore = 0;
        }
        mat[i][j] = newScore;
      }
    }
    if (ifPrintMat) {
      this.printMat(mat);
    }
//    System.out.println("... score mat calculated");
    // locate the max score in the matrix
    int resultRow = 0;
    int resultCol = 0;
    for (int i = 0; i < rowCount; i++) {
      for (int j = 0; j < colCount; j++) {
        int temp = mat[i][j];
        if (temp > this.maxScore) {
          this.maxScore = temp;
          resultRow = i;
          resultCol = j;
        }
      }
    }
//    System.out.println("... best score located");
    // backtracking
    this.cigar = this.backTracking(mat, resultRow, resultCol);
//    System.out.println("... cigar calculated");
  }

  private String backTracking(int[][] mat, int endRow, int endCol) {
    int row = endRow;
    int col = endCol;
    @SuppressWarnings("serial")
    Set<Integer> backScoreSet = new HashSet<>() {
      {
        this.add(MATCH);
        this.add(MISMATCH);
        this.add(INDEL);
      }
    };

    StringBuilder cigar = new StringBuilder();
    while (row > 0 && col > 0) {
      int tempScore = mat[row][col];
      int leftScore = mat[row][col - 1];
      int upScore = mat[row - 1][col];
      int diagScore = mat[row - 1][col - 1];

//      SmithWaterman.print(row + "," + col + " - ");
      if (backScoreSet.contains(tempScore - leftScore)) {
        if (col == 1) {
          break;
        }
        cigar.append("D");
        col--;
      } else if (backScoreSet.contains(tempScore - upScore)) {
        if (row == 1) {
          break;
        }
        cigar.append("I");
        row--;
      } else if (backScoreSet.contains(tempScore - diagScore)) {
        if (row == 1 || col == 1) {
          break;
        }
        cigar.append("M");
        col--;
        row--;
      } else {
        break;
      }
//      SmithWaterman.print(cigar.toString() + "\n");
    }
    cigar = cigar.reverse();
//    SmithWaterman.print(cigar.toString() + "\n");
//    System.out.println(row + ", " + col);
    this.resultRow = row;
    this.resultCol = col;
//    System.out.println("processing cigar...");
    return this.processCIGAR(cigar).toString();
  }

  private void printMat(int[][] mat) {
    int matRow = this.read.length() + 1;
    int matCol = this.ref.length() + 1;
    SmithWaterman.print("\t\t");
    for (int i = 0; i < this.ref.length(); i++) {
      SmithWaterman.print(this.ref.charAt(i) + "\t");
    }
    SmithWaterman.print("\n");

    for (int i = 0; i < matRow; i++) {
      if (i == 0) {
        SmithWaterman.print("\t");
      } else {
        SmithWaterman.print(this.read.charAt(i - 1) + "\t");
      }
      for (int j = 0; j < matCol; j++) {
        SmithWaterman.print(mat[i][j] + "\t");
      }
      SmithWaterman.print("\n");
    }
  }

  private StringBuilder processCIGAR(StringBuilder rawCIGAR) {
    StringBuilder cigar = new StringBuilder();
    char lastCh = rawCIGAR.charAt(0);
    char ch = rawCIGAR.charAt(0);
    int sameChCount = 0;
    for (int i = 0; i < rawCIGAR.length(); i++) {
      ch = rawCIGAR.charAt(i);
      if (ch == lastCh) {
        sameChCount++;
      } else {
        cigar.append("" + sameChCount + lastCh);
        lastCh = ch;
        sameChCount = 1;
      }
    }
    cigar.append("" + sameChCount + lastCh);
//    System.out.println(cigar.toString());
    return cigar;
  }

  private int max(int a, int b, int c) {
    return Math.max(Math.max(a, b), c);
  }

  private static void print(String str) {
    System.out.print(str);
  }
}
