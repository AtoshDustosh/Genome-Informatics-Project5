package applications;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import args.FilePath;
import toolkit.AlignResult;

class ReadAlignmentTest {
  private ReadAligner aligner = new ReadAligner(FilePath.FA.path(),
      FilePath.FASTQ.path());

  @Test
  void test1() {
    int faSequenceLength = this.aligner.faSequenceLength();
    int readsCount = this.aligner.fastqReadsCount();
    System.out
        .println("fa sequence length: " + this.aligner.faSequenceLength());
    System.out.println("fastq reads count: " + this.aligner.fastqReadsCount());

    // test1
    Random rand = new Random();
    List<Integer> refStarts = new ArrayList<>();
    List<Integer> refEnds = new ArrayList<>();
    System.out.println("random positions on the ref: ");
    for (int i = 0; i < 10; i++) {
      int refStart = rand.nextInt(faSequenceLength - 400);
      int refEnd = refStart + 400;
      refStarts.add(refStart);
      refEnds.add(refEnd);
      System.out.println("(" + (i + 1) + ") - " + refStart + ", " + refEnd);
    }

    AlignResult bestResult = new AlignResult();
    for (int i = 0; i < readsCount; i++) {
      System.out.println("read[" + (i + 1) + "]");
      for (int j = 0; j < 10; j++) {
        int readStart = 13;
        int refStart = refStarts.get(j);
        int refEnd = refEnds.get(j);
        AlignResult temp = this.aligner.alignRead(i, readStart, refStart,
            refEnd);
        if (temp.score > bestResult.score) {
          bestResult = temp;
        }
        System.out.println("\t(" + (j + 1) + ") score - " + temp.score);
      }
    }
    System.out.println(">>>>>>>>>>> best Result");
    System.out.println("\tread: " + bestResult.fastqRead.getSequence());
    System.out.println("\tref: " + bestResult.ref);
    System.out.println("\tcigar: " + bestResult.cigar);
    System.out.println("\tscore: " + bestResult.score);
  }

  @Test
  void test2() {
    System.out.println(this.aligner.faSubSequence(6535553 - 1, 6535563));
  }

}
