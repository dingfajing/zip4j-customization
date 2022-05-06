package net.lingala.customzip4j.model;

import net.lingala.customzip4j.headers.HeaderSignature;

public abstract class ZipHeader {

  private HeaderSignature signature;

  public HeaderSignature getSignature() {
    return signature;
  }

  public void setSignature(HeaderSignature signature) {
    this.signature = signature;
  }
}
