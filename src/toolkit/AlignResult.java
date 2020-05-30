package toolkit;

public class AlignResult {

  public final String ref;
  public final int score;
  public final String cigar;
  public final int pos;
  public final int startRead;
  public final int startRef;

  public final FastqRead fastqRead;

  public AlignResult() {
    this.ref = "";
    this.score = 0;
    this.cigar = "";
    this.pos = 0;
    this.startRead = 0;
    this.startRef = 0;
    this.fastqRead = null;
  }

  public AlignResult(String ref, int score, String cigar, int pos,
      int startRead, int startRef, FastqRead fastqRead) {
    this.ref = ref;
    this.score = score;
    this.cigar = cigar;
    this.startRead = startRead;
    this.startRef = startRef;
    this.pos = pos; // smith-waterman is a local alignment algorithm
    // and thus CIGAR may not start from the beginning of the read
    this.fastqRead = fastqRead;
  }

  @Override
  public String toString() {
    String str = "";
    str += "read: " + this.fastqRead.getSequence() + "\n";
    str += "ref: " + this.ref + "\n";
    str += "score: " + this.score + "\n";
    str += "cigar: " + this.cigar + "\n";
    str += "pos: " + this.pos + "\n";
    str += "startRow: " + this.startRead + ", startCol: " + this.startRef
        + "\n";
    return str;
  }
}
