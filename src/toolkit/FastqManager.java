package toolkit;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class FastqManager {

  private boolean dataLoaded = false;
  private List<FastqRead> reads = new ArrayList<>();

  public FastqManager(String filePath) {
    this.loadFastqFile(filePath);
  }

  public int readsCount() {
    return this.reads.size();
  }

  public FastqRead getRead(int index) {
    return this.reads.get(index);
  }

  private void loadFastqFile(String filePath) {
    try {
      Scanner scanner = new Scanner(new FileInputStream(filePath));
      while (scanner.hasNextLine()) {
        String identifier = scanner.nextLine();
        String sequence = scanner.nextLine();
        String comments = scanner.nextLine();
        String quality = scanner.nextLine();
        FastqRead element = new FastqRead(identifier, sequence, comments,
            quality);
        this.reads.add(element);
      }
      scanner.close();
      this.dataLoaded = true;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (NoSuchElementException e) {
      e.printStackTrace();
    }
  }
}
