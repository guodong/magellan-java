
def onPacket(pkt):
  if pkt.dstPort > 21:
    a = pkt + 1 * 2