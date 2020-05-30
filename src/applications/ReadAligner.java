package applications;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import args.FilePath;
import toolkit.AlignResult;
import toolkit.FaManager;
import toolkit.FastqManager;
import toolkit.FastqRead;
import toolkit.SmithWaterman;

public class ReadAligner {
  public static final int SEED_LENGTH = 12;

  private Map<String, Integer> seedMap = new HashMap<>();

  private FaManager faManager = null;
  private FastqManager fastqManager = null;

  public ReadAligner(String faFile, String fastqFile) {
    // load files
    this.faManager = new FaManager(faFile);
    System.out.println("... fa file loaded");
    this.fastqManager = new FastqManager(fastqFile);
    System.out.println("... fastq file loaded");
    // build hash indexes
    this.buildHashIndex(this.faManager.totalSequence());
    System.out.println("... hash index built");
//    System.out.println(this.seedMap.toString());
  }

  public static void main(String[] args) {
    ReadAligner aligner = new ReadAligner(FilePath.FA.path(),
        FilePath.FASTQ.path());
    System.out.println("fa sequence length: " + aligner.faSequenceLength());
    System.out.println("fastq reads count: " + aligner.fastqReadsCount());

    List<AlignResult> alignResults = aligner.alignAllReads();
    for (int i = 0; i < alignResults.size(); i++) {
      System.out.println(alignResults.get(i));
    }
    try {
      PrintWriter printer = new PrintWriter(
          new FileOutputStream(FilePath.SAM.path()));
      printer.write("@HD VN:1.3 SO:coordinate\n");
      printer
          .write("@SQ SN:" + aligner.faHeader().replace(">", "") + " LN:45\n");
      for (int i = 0; i < alignResults.size(); i++) {
        StringBuilder samLine = new StringBuilder();
        AlignResult result = alignResults.get(i);
        samLine.append(result.fastqRead.getIdentifier() + "\t");
        samLine.append(0 + "\t");
        samLine.append(aligner.faHeader().replace(">", "") + "\t");
        samLine.append(result.pos + "\t");
        samLine.append("*" + "\t");
        samLine.append(result.cigar + "\t");
        samLine.append("*" + "\t");
        samLine.append("*" + "\t");
        samLine.append("*" + "\t");
        samLine.append(result.fastqRead.getSequence() + "\t");
        samLine.append(result.fastqRead.getQuality() + "\t");
        samLine.append("\n");
        printer.write(samLine.toString());
        printer.flush();
      }
      printer.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public List<AlignResult> alignAllReads() {
    List<AlignResult> alignResults = new ArrayList<>();
    for (int k = 0; k < this.fastqManager.readsCount(); k++) {
//    for (int k = 0; k < 10; k++) {
      if (k % (this.fastqManager.readsCount() / 10) == 0) {
        System.out.println(
            "..." + (k / (this.fastqManager.readsCount() / 100)) + "%");
      }
      FastqRead fastqRead = this.fastqManager.getRead(k);
      String read = fastqRead.getSequence();
//      System.out.println("... aligning read-(" + (k + 1) + ")");
//      System.out.println(fastqRead);
      // creating seeds
      List<String> seeds = new ArrayList<>();
      List<Integer> seedsPosInRead = new ArrayList<>();
      for (int i = 0; i < read.length() - SEED_LENGTH; i++) {
        seeds.add(read.substring(i, i + SEED_LENGTH));
        seedsPosInRead.add(i);
//        System.out.println(seeds.get(i) + " - " + i);
      }
      // aligning all seeds of the read
      AlignResult bestResult = new AlignResult();
      for (int i = 0; i < seeds.size(); i++) {
        if (this.seedMap.containsKey(seeds.get(i))) {
          int seedPosInRead = seedsPosInRead.get(i);
          int posInRef = this.seedMap.get(seeds.get(i)) - seedPosInRead;
//          System.out.println("found match on ref at " + posInRef);
          AlignResult temp = this.alignRead(k, posInRef,
              posInRef + read.length());
          if (temp.score > bestResult.score) {
            bestResult = temp;
          }
        } else {
//          System.out
//              .println("amazing! no match for this seed - " + seeds.get(i));
        }
      } // end of aligning all seeds of the read
      alignResults.add(bestResult);
    } // end of aligning all reads
    return alignResults;
  }

  public AlignResult alignRead(int readIndex, int refStart, int refEnd) {
    int readEnd = this.fastqManager.getRead(readIndex).getSequence().length();
    return this.alignRead(readIndex, 0, readEnd, refStart, refEnd);
  }

  public AlignResult alignRead(int readIndex, int readStart, int refStart,
      int refEnd) {
    int readEnd = this.fastqManager.getRead(readIndex).getSequence().length();
    return this.alignRead(readIndex, readStart, readEnd, refStart, refEnd);

  }

  public AlignResult alignRead(int readIndex, int readStart, int readEnd,
      int refStart, int refEnd) {
    if (refEnd >= this.faManager.length() || refStart < 0) {
      return new AlignResult();
    }
    FastqRead originalRead = this.fastqManager.getRead(readIndex);
    // use "start" and "end" to cut the read
    String read = originalRead.getSequence().substring(readStart, readEnd);
    String ref = this.faManager.subSequence(refStart, refEnd);
//    System.out.println("read: " + read);
//    System.out.println("ref: " + ref);
    SmithWaterman sm = new SmithWaterman(read, ref);
    sm.execute(false);
    return new AlignResult(sm.getRef(), sm.getmaxScore(), sm.getCigar(),
        refStart, sm.getResultRow(), sm.getResultCol(), originalRead);
  }

  public int faSequenceLength() {
    return this.faManager.length();
  }

  public int fastqReadsCount() {
    return this.fastqManager.readsCount();
  }

  public String faHeader() {
    return this.faManager.faHeader();
  }

  public String subSequence(int start, int end) {
    return this.faManager.subSequence(start, end);
  }

  private void buildHashIndex(String sequence) {
    long length = sequence.length();
    for (int i = 0; i < length - ReadAligner.SEED_LENGTH; i++) {
      if (i != 0 && i % ((length - ReadAligner.SEED_LENGTH) / 10) == 0) {
        System.out.println(
            "... " + (i / ((length - ReadAligner.SEED_LENGTH) / 100)) + "%");
      }
      String seed = sequence.substring(i, i + ReadAligner.SEED_LENGTH)
          .toString();
      this.seedMap.put(seed, i);
    }
  }
}
