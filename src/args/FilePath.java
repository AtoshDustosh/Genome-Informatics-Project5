package args;

public enum FilePath {
  FA("src/data/sacCer3.fa"),
  FASTQ("src/data/DRR076693_1.1k.fastq"),
  SAM("src/data/DRR076693_1.1k.sam"),
  ;

  private String filePath = "";

  FilePath(String filePath) {
    this.filePath = filePath;
  }

  public String path() {
    return this.filePath;
  }
}
