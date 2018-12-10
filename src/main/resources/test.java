import java.util.Arrays;
import org.snlab.magellan.*;

public class MyProgram {

  private int output = 1;
  private List<Integer> whiteList = Arrays.asList(20, 21, 22, 80);

  public void onPacket(Packet pkt) {
    if (whiteList.contains(pkt.dstPort)) {
      if (pkt.dstPort < 24) {
        output = 2;
      }
    }
  }
}
