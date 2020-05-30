package toolkit;

public class FastqRead {

  private String identifier = "";
  private String sequence = "";
  private String comments = "";
  private String quality = "";

  public FastqRead(String identifier, String sequence, String comments,
      String quality) {
    this.identifier = identifier;
    this.sequence = sequence;
    this.comments = comments;
    this.quality = quality;
  }

  public String getIdentifier() {
    return this.identifier;
  }

  public String getSequence() {
    return this.sequence;

  }

  public String getComments() {
    return this.comments;
  }

  public String getQuality() {
    return this.quality;
  }

  @Override
  public String toString() {
    String str = "";
    str += this.identifier + "\n";
    str += this.sequence + "\n";
    str += this.comments + "\n";
    str += this.quality + "\n";
    return str;
  }
}
