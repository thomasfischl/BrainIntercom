package com.github.thomasfischl.brainintercom.util;

import java.io.File;

public class FileUtil {

  public static void deleteFolder(File folder) {
    if (folder.exists()) {
      for (File f : folder.listFiles()) {
        f.delete();
      }
    }
  }

}
