/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.opencl.tweaks;

//help: http://mlso.hao.ucar.edu/hao/acos/sw/cuda-sdk/shared/common.mk

import edu.syr.pcpratts.rootbeer.configuration.Configuration;
import edu.syr.pcpratts.rootbeer.util.CmdRunner;
import edu.syr.pcpratts.rootbeer.util.CudaPath;
import java.io.File;
import java.util.List;

public class GencodeOptions {

  public enum CompileArchitecture {
    Arch32bit, Arch64bit, Arch32bit64bit;
  }
  
  public String getOptions(){
    String version = getVersion();
    String sm_35;
    String sm_30;
    String sm_21;
    String sm_20;
    String sm_12;
    if(File.separator.equals("/")){
      sm_35 = "--generate-code arch=compute_35,code=\"sm_35,compute_35\" ";
      sm_30 = "--generate-code arch=compute_30,code=\"sm_30,compute_30\" ";
      sm_21 = "--generate-code arch=compute_20,code=\"sm_21,compute_20\" ";
      sm_20 = "--generate-code arch=compute_20,code=\"sm_20,compute_20\" ";
      sm_12 = "--generate-code arch=compute_12,code=\"sm_12,compute_12\" ";
    } else {
      sm_35 = "--generate-code arch=compute_35,code=\"sm_35\" ";
      sm_30 = "--generate-code arch=compute_30,code=\"sm_30\" ";
      sm_21 = "--generate-code arch=compute_20,code=\"sm_21\" ";
      sm_20 = "--generate-code arch=compute_20,code=\"sm_20\" ";  
      sm_12 = "--generate-code arch=compute_12,code=\"sm_12\" "; 
    }
    
    //sm_12 doesn't support recursion
    if(Configuration.compilerInstance().getRecursion()){
      sm_12 = "";
    }
    
    //sm_12 doesn't support doubles
    if(Configuration.compilerInstance().getDoubles()){
      sm_12 = "";
    }
    
    if(version.equals("Cuda compilation tools, release 5.5, V5.5.0")){
      return sm_35 + sm_30 + sm_21 + sm_20 + sm_12;
    } else if(version.equals("Cuda compilation tools, release 5.0, V0.2.1221")){
      return sm_35 + sm_30 + sm_21 + sm_20 + sm_12;
    } else if(version.equals("Cuda compilation tools, release 4.2, V0.2.1221")){
      return sm_30 + sm_21 + sm_20 + sm_12;
    } else if(version.equals("Cuda compilation tools, release 4.1, V0.2.1221")){
      return sm_30 + sm_21 + sm_20 + sm_12;
    } else if(version.equals("Cuda compilation tools, release 4.0, V0.2.1221")){
      return sm_30 + sm_21 + sm_20 + sm_12;
    } else if(version.equals("Cuda compilation tools, release 3.2, V0.2.1221")){
      return sm_30 + sm_21 + sm_20 + sm_12;
    } else if(version.equals("Cuda compilation tools, release 3.1, V0.2.1221")){
      return sm_20 + sm_12;
    } else if(version.equals("Cuda compilation tools, release 3.0, V0.2.1221")){
      return sm_20 + sm_12;
    } else {
      throw new RuntimeException("unsupported nvcc version. version 3.0 or higher needed. arch sm_20 or higher needed.");
    }
  }

  private String getVersion() {
    CudaPath cuda_path = new CudaPath();
    String cmd[] = new String[2];
    if(File.separator.equals("/")){
      String nvcc_path = cuda_path.get() + "nvcc";
      cmd[0] = nvcc_path;
      cmd[1] = "--version";
    } else {
      String nvcc_path = cuda_path.get();
      cmd[0] = nvcc_path;
      cmd[1] = "--version";
    }
    
    CmdRunner runner = new CmdRunner();
    runner.run(cmd, new File("."));
    List<String> lines = runner.getOutput();
    if(lines.isEmpty()){
      List<String> error_lines = runner.getError();
      for(String error_line : error_lines){
        System.out.println(error_line);
      }
      throw new RuntimeException("error detecting nvcc version.");
    }
    
    String last_line = lines.get(lines.size()-1);
    return last_line;
  }
  
}
