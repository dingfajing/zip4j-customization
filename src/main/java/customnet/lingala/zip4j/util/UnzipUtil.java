package customnet.lingala.zip4j.util;

import customnet.lingala.zip4j.exception.ZipException;
import customnet.lingala.zip4j.io.inputstream.NumberedSplitInputStream;
import customnet.lingala.zip4j.io.inputstream.SplitInputStream;
import customnet.lingala.zip4j.io.inputstream.ZipInputStream;
import customnet.lingala.zip4j.io.inputstream.ZipStandardSplitInputStream;
import customnet.lingala.zip4j.model.FileHeader;
import customnet.lingala.zip4j.model.ZipModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static customnet.lingala.zip4j.util.FileUtils.setFileAttributes;
import static customnet.lingala.zip4j.util.FileUtils.setFileLastModifiedTime;
import static customnet.lingala.zip4j.util.FileUtils.setFileLastModifiedTimeWithoutNio;

public class UnzipUtil {

  public static ZipInputStream createZipInputStream(ZipModel zipModel, FileHeader fileHeader, char[] password)
      throws IOException {

    SplitInputStream splitInputStream = null;
    try {
      splitInputStream = createSplitInputStream(zipModel);
      splitInputStream.prepareExtractionForFileHeader(fileHeader);

      ZipInputStream zipInputStream = new ZipInputStream(splitInputStream, password);
      if (zipInputStream.getNextEntry(fileHeader, false) == null) {
        throw new ZipException("Could not locate local file header for corresponding file header");
      }

      return zipInputStream;
    } catch (IOException e) {
      if (splitInputStream != null) {
        splitInputStream.close();
      }
      throw e;
    }
  }

  public static void applyFileAttributes(FileHeader fileHeader, File file) {

    try {
      Path path = file.toPath();
      setFileAttributes(path, fileHeader.getExternalFileAttributes());
      setFileLastModifiedTime(path, fileHeader.getLastModifiedTime());
    } catch (NoSuchMethodError e) {
      setFileLastModifiedTimeWithoutNio(file, fileHeader.getLastModifiedTime());
    }
  }

  public static SplitInputStream createSplitInputStream(ZipModel zipModel) throws IOException {
    File zipFile = zipModel.getZipFile();

    if (zipFile.getName().endsWith(InternalZipConstants.SEVEN_ZIP_SPLIT_FILE_EXTENSION_PATTERN)) {
      return new NumberedSplitInputStream(zipModel.getZipFile(), true,
          zipModel.getEndOfCentralDirectoryRecord().getNumberOfThisDisk());
    }

    return new ZipStandardSplitInputStream(zipModel.getZipFile(), zipModel.isSplitArchive(),
        zipModel.getEndOfCentralDirectoryRecord().getNumberOfThisDisk());
  }

}
