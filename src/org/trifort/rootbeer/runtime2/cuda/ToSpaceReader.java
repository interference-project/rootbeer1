/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.runtime2.cuda;

import java.util.List;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.Memory_old;
import org.trifort.rootbeer.runtime.Serializer;


public class ToSpaceReader {

  private BlockingQueue<InputItem> m_InputQueue;
  private BlockingQueue<InputItem> m_OutputQueue;
  private Thread m_Thread;
  
  public ToSpaceReader(){
    m_InputQueue = new BlockingQueue<InputItem>();
    m_OutputQueue = new BlockingQueue<InputItem>();
    ReadThreadProc proc = new ReadThreadProc(m_InputQueue, m_OutputQueue);
    m_Thread = new Thread(proc);
    m_Thread.setDaemon(true);
    m_Thread.start();
  }
  
  public void read(List<Kernel> items, List<Long> handles, Serializer visitor){
    InputItem item = new InputItem();
    item.m_Items = items;
    item.m_HandlesCache = handles;
    item.m_Visitor = visitor;
    m_InputQueue.put(item);
  }
  
  public void join(){
    m_OutputQueue.take();
  }
  
  private class InputItem {
    public List<Kernel> m_Items;
    public List<Long> m_HandlesCache;
    public Serializer m_Visitor;
  }
    
  private class ReadThreadProc implements Runnable {
       
    private BlockingQueue<InputItem> m_InputQueue;
    private BlockingQueue<InputItem> m_OutputQueue;
  
    public ReadThreadProc(BlockingQueue<InputItem> input_queue,
      BlockingQueue<InputItem> output_queue){
      
      m_InputQueue = input_queue;
      m_OutputQueue = output_queue;
    }
        
    public void run(){
      while(true){
        InputItem input_item = m_InputQueue.take();
        for(int i = 0; i < input_item.m_Items.size(); ++i){
          Kernel item = input_item.m_Items.get(i);
          long handle = input_item.m_HandlesCache.get(i);
          
          try {
            Kernel new_item = (Kernel) input_item.m_Visitor.readFromHeap(item, true, handle);
            input_item.m_Items.set(i, new_item);
          } catch(Exception ex){
            ex.printStackTrace();
            input_item.m_Items.set(i, null);
          }
        }
        m_OutputQueue.put(input_item);
      }
    }
  } 
}
